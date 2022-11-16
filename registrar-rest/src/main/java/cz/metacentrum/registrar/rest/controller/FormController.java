package cz.metacentrum.registrar.rest.controller;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forms")
public class FormController {

	private FormService formService;

	@Autowired
	public FormController(FormService formService) {
		this.formService = formService;
	}

	@PostMapping("/form")
	public Form createForm(final @RequestBody Form form) {
		return formService.createForm(form);
	}

	@PutMapping("/form")
	public Form updateForm(final @RequestBody Form form) {
		return formService.updateForm(form);
	}

	@DeleteMapping("/form/{id}")
	public void deleteForm(@PathVariable Long id) {
		formService.deleteForm(id);
	}
}
