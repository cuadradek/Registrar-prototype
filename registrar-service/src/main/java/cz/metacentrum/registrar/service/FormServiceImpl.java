package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.persistence.repository.FormItemRepository;
import cz.metacentrum.registrar.persistence.repository.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FormServiceImpl implements FormService {

	private final FormRepository formRepository;
	private final FormItemRepository formItemRepository;

	@Autowired
	public FormServiceImpl(FormRepository formRepository, FormItemRepository formItemRepository) {
		this.formRepository = formRepository;
		this.formItemRepository = formItemRepository;
	}

	@Override
	// TODO return optional or exception or null????
//	exception:
//	optional: vyhoda, ze nebudem mat vsade checked exception boiler plate kod
//	null:
	public Optional<Form> getFormById(Long id) {
		return formRepository.findById(id);
//				.orElseThrow(() -> new FormNotFoundException(id));
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
//			System.out.println(LocalDateTime.now());
//			throw ex;
		}
	}

	@Override
	public Form updateForm(Form form) {
		return formRepository.findById(form.getId())
				.map(savedForm -> {
					savedForm.setAutoApprove(form.isAutoApprove());
					savedForm.setCanBeResubmitted(form.isCanBeResubmitted());
					savedForm.setIdmFormManagersGroup(form.getIdmFormManagersGroup());
					savedForm.setName(form.getName());
					savedForm.setIdmObject(form.getIdmObject());
					savedForm.setRedirectUrl(form.getRedirectUrl());
					savedForm.setApprovalGroups(form.getApprovalGroups());
					return formRepository.save(savedForm);
				}).orElseThrow();
	}

	@Override
	public List<FormItem> getFormItems(Long formId) {
		Form form = getFormById(formId)
				.orElseThrow(() -> new FormNotFoundException(formId));
		return formItemRepository.getAllByForm(form);
	}

	@Override
	public List<FormItem> createFormItems(Long formId, List<FormItem> formItems) {
		Form form = getFormById(formId)
				.orElseThrow(() -> new FormNotFoundException(formId));
		formItems.forEach(formItem -> formItem.setForm(form));
		return formItemRepository.saveAll(formItems);
	}
}
