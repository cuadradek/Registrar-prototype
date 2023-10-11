package cz.metacentrum.registrar.persistence.repository;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmittedFormRepository extends JpaRepository<SubmittedForm, Long> {
	List<SubmittedForm> findSubmittedFormsByForm(Form form);
	List<SubmittedForm> findSubmittedFormsByFormAndFormState(Form form, Form.FormState state);
}
