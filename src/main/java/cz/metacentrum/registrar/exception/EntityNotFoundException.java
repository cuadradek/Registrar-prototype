package cz.metacentrum.registrar.exception;


public class EntityNotFoundException extends RuntimeException {
	public EntityNotFoundException(String message) {
		super(message);
	}
}
