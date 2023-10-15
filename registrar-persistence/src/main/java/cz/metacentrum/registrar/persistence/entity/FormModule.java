package cz.metacentrum.registrar.persistence.entity;

import java.util.Map;

// maybe implement this as abstract class and have config options as the only field, maybe friendly name as well..
public interface FormModule {
	SubmittedForm beforeApprove(SubmittedForm submittedForm);
	SubmittedForm onApprove(SubmittedForm submittedForm);
	SubmittedForm onReject(SubmittedForm submittedForm);
	SubmittedForm onLoad(SubmittedForm submittedForm, Map<String, String> configOptions);
}
