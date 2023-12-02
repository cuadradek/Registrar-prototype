package cz.metacentrum.registrar.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum FormState {
	PENDING_VERIFICATION,
	PENDING_APPROVAL,
	PRE_APPROVED,
	APPROVED,
	REJECTED,
	CHANGES_REQUESTED;

	public static final Set<FormState> DECISION_POSSIBLE_STATES = Collections.unmodifiableSet(EnumSet.of(
			PENDING_APPROVAL,
			CHANGES_REQUESTED
	));

	public static final Set<FormState> OPEN_FORM_STATES = Collections.unmodifiableSet(EnumSet.of(
			PENDING_VERIFICATION,
			PENDING_APPROVAL,
			CHANGES_REQUESTED,
			PRE_APPROVED
	));

	public boolean isOpenFormState() {
		return OPEN_FORM_STATES.contains(this);
	}

	public boolean canMakeDecision() {
		return DECISION_POSSIBLE_STATES.contains(this);
	}
}
