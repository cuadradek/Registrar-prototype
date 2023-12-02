package cz.metacentrum.registrar.persistence.entity;

import java.util.List;
import java.util.Map;

// maybe implement this as abstract class and have config options as the only field, maybe friendly name as well..
public interface FormModule {
	List<String> getConfigOptions();
	void beforeApprove(SubmittedForm submittedForm);
	void onApprove(SubmittedForm submittedForm, Map<String, String> configOptions);
	void onReject(SubmittedForm submittedForm);
	List<SubmittedForm> onLoad(SubmittedForm submittedForm, Map<String, String> configOptions);
	boolean hasRightToAddToForm(Form form, Map<String, String> configOptions);
}
