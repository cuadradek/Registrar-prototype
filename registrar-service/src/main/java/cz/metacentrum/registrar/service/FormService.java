package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;

public interface FormService {

	Form createForm(Form form);
	void deleteForm(Form form);
	Form updateForm(Form form);
}
