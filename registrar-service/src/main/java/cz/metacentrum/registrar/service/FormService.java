package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;

public interface FormService {

	Form createForm(Form form);
	void deleteForm(Long id);
	Form updateForm(Form form);
}
