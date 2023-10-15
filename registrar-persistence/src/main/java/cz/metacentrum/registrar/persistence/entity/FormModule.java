package cz.metacentrum.registrar.persistence.entity;

import java.util.List;
import java.util.Map;

// maybe implement this as abstract class and have config options as the only field, maybe friendly name as well..
public interface FormModule {
	Map<String, String> getConfigOptions();
	SubmittedForm beforeApprove(SubmittedForm submittedForm);
	SubmittedForm onApprove(SubmittedForm submittedForm, Map<String, String> configOptions);
	SubmittedForm onReject(SubmittedForm submittedForm);
	List<SubmittedForm> onLoad(SubmittedForm submittedForm, Map<String, String> configOptions);
}
