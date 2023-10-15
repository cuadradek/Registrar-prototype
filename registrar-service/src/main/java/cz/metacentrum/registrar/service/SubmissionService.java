package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.persistence.entity.SubmissionResult;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubmissionService {
	Optional<Submission> findSubmissionById(Long id);
	Optional<SubmittedForm> findSubmittedFormById(Long id);
	List<SubmittedForm> findSubmittedFormsByForm(Form form);
	List<SubmittedForm> findSubmittedFormsByFormAndState(Form form, Form.FormState state);
	SubmissionResult createSubmission(Submission submission);

	void submitAutoForm(Submission submission, AssignedFlowForm a);

	SubmittedForm approveSubmittedForm(Long id);
	SubmittedForm rejectSubmittedForm(Long id, String message);

	Submission loadSubmission(Collection<Form> forms);

	Submission loadSubmission(Form form);

	List<SubmittedForm> getSubmittedFormsBySubmitterId(String submitterId);
}
