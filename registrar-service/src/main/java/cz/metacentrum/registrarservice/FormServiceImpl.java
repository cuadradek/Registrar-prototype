package cz.metacentrum.registrarservice;

import cz.metacentrum.registrarpersistence.entity.Form;
import cz.metacentrum.registrarpersistence.repository.FormRepository;
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
	public void deleteForm(Form form) {
		formRepository.deleteById(form.getId());
	}

	@Override
	public Form updateForm(Form form) {
		return formRepository.save(form);
	}
}
