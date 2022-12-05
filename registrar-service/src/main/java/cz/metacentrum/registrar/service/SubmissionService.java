package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.Submission;

import java.util.List;
import java.util.Optional;

public interface SubmissionService {
	Optional<Submission> findSubmissionById(Long id);
	List<Submission> findSubmissionsByForm(Form form);
	List<Submission> findSubmissionsByFormAndState(Form form, Form.FormState state);
	Submission createSubmission(Submission submission);

	Submission approveSubmission(Long id);

	Submission loadSubmission(Long formId);
}
