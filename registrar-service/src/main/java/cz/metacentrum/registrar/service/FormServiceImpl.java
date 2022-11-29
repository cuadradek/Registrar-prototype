package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.repository.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FormServiceImpl implements FormService {

	private final FormRepository formRepository;

	@Autowired
	public FormServiceImpl(FormRepository formRepository) {
		this.formRepository = formRepository;
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
					return formRepository.save(savedForm);
				}).orElseThrow();
	}
}