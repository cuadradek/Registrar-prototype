package cz.metacentrum.registrar.persistence.entity;

// maybe implement this as abstract class and have config options as the only field, maybe friendly name as well..
public interface FormModule {
	SubmittedForm beforeApprove(SubmittedForm submittedForm);
	SubmittedForm onApprove(SubmittedForm submittedForm);
	SubmittedForm onReject(SubmittedForm submittedForm);
}
