package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Approval;
import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormState;
import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.persistence.entity.SubmissionResult;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubmissionService {
	Optional<Submission> findSubmissionById(Long id);
	Optional<SubmittedForm> findSubmittedFormById(Long id);
	List<SubmittedForm> findSubmittedFormsByForm(Form form);
	List<SubmittedForm> findSubmittedFormsByFormAndState(Form form, FormState state);
	SubmissionResult createSubmission(Submission submission);

	void submitAutoForm(Submission submission, AssignedFlowForm a);

	SubmittedForm makeApprovalDecision(SubmittedForm submittedForm, Approval.Decision decision, String message);

	SubmittedForm approveSubmittedForm(Long id);
	SubmittedForm rejectSubmittedForm(Long id, String message);

	Submission loadSubmission(Collection<Form> forms);

	Submission loadSubmission(Form form, @Nullable String redirectUrl);

	List<SubmittedForm> loadSubmittedForm(Form form);

	List<SubmittedForm> getSubmittedFormsBySubmitterId(String submitterId);
}
