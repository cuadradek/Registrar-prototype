package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.exception.FormNotFoundException;
import cz.metacentrum.registrar.model.ApprovalGroup;
import cz.metacentrum.registrar.model.AssignedFlowForm;
import cz.metacentrum.registrar.model.AssignedFormModule;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormItem;
import cz.metacentrum.registrar.repository.ApprovalGroupRepository;
import cz.metacentrum.registrar.repository.FlowFormRepository;
import cz.metacentrum.registrar.repository.FormItemRepository;
import cz.metacentrum.registrar.repository.FormModuleRepository;
import cz.metacentrum.registrar.repository.FormRepository;
import cz.metacentrum.registrar.service.iam.FormModule;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
	private final IAMFormItemsLoader iamFormItemsLoader;
	private final FormModuleRepository formModulesRepository;
	private final ApprovalGroupRepository approvalGroupRepository;
	private final ApplicationContext context;

	@Autowired
	public FormServiceImpl(FormRepository formRepository, FormItemRepository formItemRepository, FlowFormRepository flowFormRepository,
						   IAMFormItemsLoader iamFormItemsLoader, FormModuleRepository formModulesRepository, ApprovalGroupRepository approvalGroupRepository, ApplicationContext context) {
		this.formRepository = formRepository;
		this.formItemRepository = formItemRepository;
		this.flowFormRepository = flowFormRepository;
		this.iamFormItemsLoader = iamFormItemsLoader;
		this.formModulesRepository = formModulesRepository;
		this.approvalGroupRepository = approvalGroupRepository;
		this.context = context;
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

		formItems.forEach(item -> {
			if (item.getId() != null && !existingItemsIds.contains(item.getId())) {
				throw new IllegalArgumentException("Cannot change form items from another form!");
			}
			iamFormItemsLoader.validateItem(item);
			item.setForm(form);
		});

		//possibly hard delete formItems marked as deleted if such formItem is not used in any submitted form

		return formItemRepository.saveAll(formItems);
	}

	@Override
	public List<Long> getFormsByIdmApprovalGroups(Set<UUID> groupUUIDs) {
		return formRepository.findIdsByIamApprovalGroups(groupUUIDs);
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
	public List<AssignedFormModule> getAssignedModules(Long formId) {
		Form form = formRepository.getReferenceById(formId);
		return formModulesRepository.getAllByForm(form).stream()
				.sorted()
				.map(this::setModule)
				.toList();
	}

	private AssignedFormModule setModule(AssignedFormModule assignedModule) {
		try {
			FormModule formModule = context.getBean(assignedModule.getModuleName(), FormModule.class);
			assignedModule.setFormModule(formModule);
			return assignedModule;
		} catch (BeansException ex) {
			throw new IllegalArgumentException("Non existing form module: " + assignedModule.getModuleName());
		}
	}

	@Override
	public List<AssignedFormModule> setAssignedModules(Long formId, List<AssignedFormModule> modules) {
		Form form = getFormById(formId).orElseThrow(() -> new FormNotFoundException(formId));
		var existingModulesIds = getAssignedModules(formId).stream().map(AssignedFormModule::getId).collect(Collectors.toSet());
		var updatingModulesIds = modules.stream().map(AssignedFormModule::getId).collect(Collectors.toSet());

		modules.forEach(assignedModule -> {
			if (assignedModule.getId() != null && !existingModulesIds.contains(assignedModule.getId())) {
				throw new IllegalArgumentException("Cannot change assignment for different form!");
			}
			assignedModule.setForm(form);
		});

		// delete assigned modules missing in input list
		// (possibly change this - add transient "forDelete" to AssignedFormModule and delete only assignments marked with this flag)
		existingModulesIds.removeAll(updatingModulesIds);
		if (!existingModulesIds.isEmpty()) {
			formModulesRepository.deleteAllById(existingModulesIds);
		}

		return formModulesRepository.saveAll(modules);
	}

	@Override
	public List<ApprovalGroup> getApprovalGroups(Long formId) {
		Form form = formRepository.getReferenceById(formId);
		return approvalGroupRepository.getAllByForm(form);
	}

	@Override
	public List<ApprovalGroup> setApprovalGroups(Long formId, List<ApprovalGroup> groups) {
		Form form = getFormById(formId).orElseThrow(() -> new FormNotFoundException(formId));
		var existingGroupsIds = getApprovalGroups(formId).stream().map(ApprovalGroup::getId).collect(Collectors.toSet());
		var updatingGroupsIds = groups.stream().map(ApprovalGroup::getId).collect(Collectors.toSet());

		groups.forEach(approvalGroup -> {
			if (approvalGroup.getId() != null && !existingGroupsIds.contains(approvalGroup.getId())) {
				throw new IllegalArgumentException("Cannot change assignment for different form!");
			}
			approvalGroup.setForm(form);
		});

		// delete approval groups missing in input list
		// (possibly change this - add transient "forDelete" to ApprovalGroup and delete only groups marked with this flag)
		existingGroupsIds.removeAll(updatingGroupsIds);
		if (!existingGroupsIds.isEmpty()) {
			approvalGroupRepository.deleteAllById(existingGroupsIds);
		}

		return approvalGroupRepository.saveAll(groups);
	}

	@Override
	public List<Form> getFormsByIdmObject(UUID idmObject) {
		return formRepository.getFormsByIamObject(idmObject);
	}

	@Override
	public List<Long> getFormsByIdmManagersGroups(Set<UUID> groupUUIDs) {
		return formRepository.findIdsByIamFormManagersGroup(groupUUIDs);
	}
}
