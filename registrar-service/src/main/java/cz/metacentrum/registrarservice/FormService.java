package cz.metacentrum.registrarservice;

import cz.metacentrum.registrarpersistence.entity.Form;

public interface FormService {

	Form createForm(Form form);
	void deleteForm(Form form);
	Form updateForm(Form form);
}
