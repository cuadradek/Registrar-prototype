package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.repository.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormServiceImpl implements FormService {

	private FormRepository formRepository;

	@Autowired
	public FormServiceImpl(FormRepository formRepository) {
		this.formRepository = formRepository;
	}

	@Override
	public Form createForm(Form form) {
		return formRepository.save(form);
	}

	@Override
	public void deleteForm(Long id) {
		formRepository.deleteById(id);
	}

	@Override
	public Form updateForm(Form form) {
		return formRepository.save(form);
	}
}
