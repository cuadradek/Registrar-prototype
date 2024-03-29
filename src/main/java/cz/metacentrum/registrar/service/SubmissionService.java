package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.model.Approval;
import cz.metacentrum.registrar.model.AssignedFlowForm;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormState;
import cz.metacentrum.registrar.model.Submission;
import cz.metacentrum.registrar.model.SubmissionResult;
import cz.metacentrum.registrar.model.SubmittedForm;
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

	/**
	 * Consolidates user submissions to link them into his newly generated submitterId.
	 * Sets submitterId for all submission with given originalIdentityIdentifier
	 * and originalIdentityIssuer.
	 * @param submitterId
	 * @param originalIdentityIdentifier
	 * @param originalIdentityIssuer
	 */
	void consolidateSubmissions(String submitterId, String originalIdentityIdentifier, String originalIdentityIssuer);

	SubmittedForm createApproval(Approval approval);

	Submission loadSubmission(Collection<Form> forms);

	Submission loadSubmission(Form form, @Nullable String redirectUrl);

	List<SubmittedForm> loadSubmittedForm(Form form);

	List<SubmittedForm> getSubmittedFormsBySubmitterId(String submitterId);
}
