package cz.metacentrum.registrar.exception;

public class SubmittedFormNotFoundException extends EntityNotFoundException {
	public SubmittedFormNotFoundException(Long id) {
		super("Could not find submitted form with id: " + id);
	}

	public SubmittedFormNotFoundException(String message) {
		super("Could not submitted form: " + message);
	}
}
