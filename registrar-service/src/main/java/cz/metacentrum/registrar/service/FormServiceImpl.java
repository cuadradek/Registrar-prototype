package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.persistence.repository.FlowFormRepository;
import cz.metacentrum.registrar.persistence.repository.FormItemRepository;
import cz.metacentrum.registrar.persistence.repository.FormRepository;
import cz.metacentrum.registrar.service.formitems.FormItemsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FormServiceImpl implements FormService {

	private final FormRepository formRepository;
	private final FormItemRepository formItemRepository;
	private final FlowFormRepository flowFormRepository;
	private final FormItemsLoader formItemsLoader;

	@Autowired
	public FormServiceImpl(FormRepository formRepository, FormItemRepository formItemRepository, FlowFormRepository flowFormRepository, FormItemsLoader formItemsLoader) {
		this.formRepository = formRepository;
		this.formItemRepository = formItemRepository;
		this.flowFormRepository = flowFormRepository;
		this.formItemsLoader = formItemsLoader;
	}

	@Override
	public Optional<Form> getFormById(Long id) {
		return formRepository.findById(id);
	}

	@Override
	public List<Form> getFormsByIds(Set<Long> ids) {
		List<Form> forms = formRepository.getAllByIdIn(ids);

		if (forms.size() != ids.size()) {
			Set<Long> foundIds = forms.stream()
					.map(Form::getId)
					.collect(Collectors.toSet());
			Set<Long> notFoundIds = new HashSet<>(ids);
			notFoundIds.removeAll(foundIds);
			throw new FormNotFoundException(notFoundIds);
		}

		return forms;
	}

	@Override
	public List<Form> getAllForms() {
		return formRepository.findAll();
	}

	@Override
	public Form createForm(Form form) {
		return formRepository.save(form);
	}

	@Override
	public void deleteForm(Long id) {
		try {
			formRepository.deleteById(id);
		} catch (EmptyResultDataAccessException ex) {
			throw new FormNotFoundException(id);
		}
	}

	@Override
	public Form updateForm(Form form) {
		formRepository.findById(form.getId()).orElseThrow(() -> new FormNotFoundException(form.getId()));
		return formRepository.save(form);
	}

	@Override
	public List<FormItem> getFormItems(Long formId) {
//		Form form = getFormById(formId)
//				.orElseThrow(() -> new FormNotFoundException(formId));
		Form form = formRepository.getReferenceById(formId);
		return formItemRepository.getAllByFormAndIsDeleted(form, false);
	}

	@Override
	public List<FormItem> setFormItems(Long formId, List<FormItem> formItems) {
		Form form = getFormById(formId).orElseThrow(() -> new FormNotFoundException(formId));
		var existingItemsIds = getFormItems(formId).stream().map(FormItem::getId).collect(Collectors.toSet());

		formItems.forEach(formItem -> {
			if (formItem.getId() != null && !existingItemsIds.contains(formItem.getId())) {
				throw new IllegalArgumentException("Cannot change form item from different form!");
			}
			formItemsLoader.validateItem(formItem);
			formItem.setForm(form);
		});

		//possibly hard delete formItems marked as deleted if such formItem is not used in any submitted form

		return formItemRepository.saveAll(formItems);
	}

	@Override
	public List<Long> getFormsByIdmApprovalGroups(Set<UUID> groupUUIDs) {
		return formRepository.findIsByIdmApprovalGroups(groupUUIDs);
	}

	@Override
	public List<AssignedFlowForm> getAssignedFlowForms(Long mainFormId) {
		Form mainForm = formRepository.getReferenceById(mainFormId);
		return flowFormRepository.getAllByMainForm(mainForm);
	}

	@Override
	public List<AssignedFlowForm> setAssignedFlowForms(Long mainFormId, List<AssignedFlowForm> assignedFlowForms) {
		Form mainForm = getFormById(mainFormId).orElseThrow(() -> new FormNotFoundException(mainFormId));
		var existingFlowsIds = getAssignedFlowForms(mainFormId).stream().map(AssignedFlowForm::getId).collect(Collectors.toSet());
		var updatingItemsIds = assignedFlowForms.stream().map(AssignedFlowForm::getId).collect(Collectors.toSet());

		assignedFlowForms.forEach(assignedFlowForm -> {
			if (assignedFlowForm.getId() != null && !existingFlowsIds.contains(assignedFlowForm.getId())) {
				throw new IllegalArgumentException("Cannot change flow assignment for different main form!");
			}
//			TODO: check self-assignment, multiple assignments to 1 form, ...
			if (mainFormId.equals(assignedFlowForm.getFlowForm().getId())) {
				throw new IllegalArgumentException("Cannot create self flow-assignment!");
			}
			assignedFlowForm.setMainForm(mainForm);
		});

		// delete flow assignments missing in input list
		// (possibly change this - add transient "forDelete" to AssignedFlowForm and delete only assignments marked with this flag)
		existingFlowsIds.removeAll(updatingItemsIds);
		if (!existingFlowsIds.isEmpty()) {
			flowFormRepository.deleteAllById(existingFlowsIds);
		}

		return flowFormRepository.saveAll(assignedFlowForms);
	}

	@Override
	public Optional<Form> getFormByUrlSuffix(String urlSuffix) {
		return formRepository.getFormByUrlSuffix(urlSuffix);
	}

	@Override
	public List<Long> getFormsByIdmManagersGroups(Set<UUID> groupUUIDs) {
		return formRepository.findIdsByIdmFormManagersGroup(groupUUIDs);
	}
}
