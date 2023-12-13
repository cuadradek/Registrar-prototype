package cz.metacentrum.registrar.service.iam;

import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.SubmittedForm;

import java.util.List;
import java.util.Map;

public interface FormModule {
	/**
	 * Returns list of config option names that must be set.
	 */
	List<String> getConfigOptions();

	/**
	 * Module's logic that should be executed before a submitted form is approved.
	 * @param submittedForm
	 */
	void beforeApprove(SubmittedForm submittedForm);

	/**
	 * Module's logic that should be executed when a submitted form is approved.
	 * @param submittedForm
	 */
	void onApprove(SubmittedForm submittedForm, Map<String, String> configOptions);

	/**
	 * Module's logic that should be executed when a submitted form is rejected.
	 * @param submittedForm
	 */
	void onReject(SubmittedForm submittedForm);

	/**
	 * Module's logic that should be executed when a submitted form is loaded.
	 * @param submittedForm
	 */
	List<SubmittedForm> onLoad(SubmittedForm submittedForm, Map<String, String> configOptions);

	/**
	 * Validates that a caller has right to assign this module to the form
	 * with the given config options
	 * @param form
	 * @param configOptions
	 * @return true if the caller has right to assign this module
	 */
	boolean hasRightToAddToForm(Form form, Map<String, String> configOptions);
}
