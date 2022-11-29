package cz.metacentrum.registrar.service;

import java.util.Set;

// I would change this to checked exception TODO
public class FormNotFoundException extends RuntimeException {
	public FormNotFoundException(Long id) {
		super("Could not find form " + id);
	}

	public FormNotFoundException(Set<Long> ids) {
		super("Could not find forms " + ids);
	}
}
