package cz.metacentrum.registrar.repository;

import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormState;
import cz.metacentrum.registrar.model.SubmittedForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmittedFormRepository extends JpaRepository<SubmittedForm, Long> {
	List<SubmittedForm> findSubmittedFormsByForm(Form form);
	List<SubmittedForm> findSubmittedFormsByFormAndFormState(Form form, FormState state);
}
