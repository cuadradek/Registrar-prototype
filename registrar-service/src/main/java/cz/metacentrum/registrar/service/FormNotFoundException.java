package cz.metacentrum.registrar.service;

// I would change this to checked exception TODO
public class FormNotFoundException extends RuntimeException {
	public FormNotFoundException(Long id) {
		super("Could not find form " + id);
	}
}
