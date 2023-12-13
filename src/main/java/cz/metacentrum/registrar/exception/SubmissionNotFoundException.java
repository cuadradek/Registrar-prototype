package cz.metacentrum.registrar.exception;

import java.util.Set;

public class SubmissionNotFoundException extends RuntimeException {
	public SubmissionNotFoundException(Long id) {
		super("Could not find submission with id: " + id);
	}

	public SubmissionNotFoundException(String message) {
		super("Could not find submission: " + message);
	}
}
