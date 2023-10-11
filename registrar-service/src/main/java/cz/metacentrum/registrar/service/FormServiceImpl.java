package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
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
	private final FormItemsLoader formItemsLoader;

	@Autowired
	public FormServiceImpl(FormRepository formRepository, FormItemRepository formItemRepository, FormItemsLoader formItemsLoader) {
		this.formRepository = formRepository;
		this.formItemRepository = formItemRepository;
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
		return formItemRepository.getAllByForm(form);
	}

	@Override
	public List<FormItem> createFormItems(Long formId, List<FormItem> formItems) {
		Form form = getFormById(formId)
				.orElseThrow(() -> new FormNotFoundException(formId));
		formItems.forEach(formItem -> {
			formItemsLoader.validateItem(formItem);
			formItem.setForm(form);
		});
		return formItemRepository.saveAll(formItems);
	}

	@Override
	public List<Long> getFormsByIdmApprovalGroups(Set<UUID> groupUUIDs) {
		return formRepository.findIsByIdmApprovalGroups(groupUUIDs);
	}

	@Override
	public List<Long> getFormsByIdmManagersGroups(Set<UUID> groupUUIDs) {
		return formRepository.findIdsByIdmFormManagersGroup(groupUUIDs);
	}
}
