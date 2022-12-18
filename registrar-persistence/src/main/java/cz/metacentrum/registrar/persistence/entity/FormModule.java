package cz.metacentrum.registrar.persistence.entity;

// maybe implement this as abstract class and have config options as the only field, maybe friendly name as well..
public interface FormModule {
	Submission beforeApprove(Submission submission);
	Submission onApprove(Submission submission);
	Submission onReject(Submission submission);
}
