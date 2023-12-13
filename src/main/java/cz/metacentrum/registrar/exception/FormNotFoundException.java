package cz.metacentrum.registrar.exception;

import java.util.Set;

public class FormNotFoundException extends RuntimeException {
	public FormNotFoundException(Long id) {
		super("Could not find form with id: " + id);
	}

	public FormNotFoundException(Set<Long> ids) {
		super("Could not find forms " + ids);
	}

	public FormNotFoundException(String message) {
		super("Could not find form: " + message);
	}
}
