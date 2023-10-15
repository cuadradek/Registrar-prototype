package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;

import java.util.List;
import java.util.Optional;

public interface SubmissionService {
	Optional<Submission> findSubmissionById(Long id);
	Optional<SubmittedForm> findSubmittedFormById(Long id);
	List<SubmittedForm> findSubmittedFormsByForm(Form form);
	List<SubmittedForm> findSubmittedFormsByFormAndState(Form form, Form.FormState state);
	Submission createSubmission(Submission submission);

	SubmittedForm approveSubmittedForm(Long id);
	SubmittedForm rejectSubmittedForm(Long id, String message);

	Submission loadSubmission(Form form);

	List<SubmittedForm> getSubmittedFormsBySubmitterId(String submitterId);
}
