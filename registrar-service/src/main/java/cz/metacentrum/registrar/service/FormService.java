package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;

import java.util.List;
import java.util.Optional;

public interface FormService {

	Optional<Form> getFormById(Long id);
	List<Form> getAllForms();
	Form createForm(Form form);
	void deleteForm(Long id);
	Form updateForm(Form form);
}
