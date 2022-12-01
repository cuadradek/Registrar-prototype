package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FormService {

	Optional<Form> getFormById(Long id);
	List<Form> getFormsByIds(@NonNull Set<Long> ids);
	List<Form> getAllForms();
	Form createForm(Form form);
	void deleteForm(Long id);
	Form updateForm(Form form);

	List<FormItem> getFormItems(Long formId);

	List<FormItem> createFormItems(Long formId, List<FormItem> formItems);
}
