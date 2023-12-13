package cz.metacentrum.registrar.exception;

public class SubmissionNotFoundException extends EntityNotFoundException {
	public SubmissionNotFoundException(Long id) {
		super("Could not find submission with id: " + id);
	}

	public SubmissionNotFoundException(String message) {
		super("Could not find submission: " + message);
	}
}
