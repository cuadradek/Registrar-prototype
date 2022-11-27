package cz.metacentrum.registrar.rest.controller;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.service.FormNotFoundException;
import cz.metacentrum.registrar.service.FormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping("/api")
public class FormController {

	private FormService formService;

	@Autowired
	public FormController(FormService formService) {
		this.formService = formService;
	}

	@GetMapping("/forms")
	List<Form> getAllForms() {
		return formService.getAllForms();
	}

	@GetMapping("/forms/{id}")
//	/customers/{customerId}/accounts/{accountId}
//	http://api.example.com/user-management/users/admin - singular
	public ResponseEntity<Form> getFormById(@PathVariable Long id) {
		Optional<Form> formData = formService.getFormById(id);
		return formData
				.map(form -> new ResponseEntity<>(form, HttpStatus.OK))
				.orElseThrow(() -> new FormNotFoundException(id));
//				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//		return new ResponseEntity<>(formService.getFormById(id), HttpStatus.OK);
//		catch (Exception e) {
//			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
	}

	@PostMapping("/forms")
	public ResponseEntity<Form> createForm(final @RequestBody Form form) {
		return new ResponseEntity<>(formService.createForm(form), HttpStatus.CREATED);
//		catch (Exception e) {
//      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
	}

	@PutMapping("/forms")
	public ResponseEntity<Form> updateForm(final @RequestBody Form form) {
		return new ResponseEntity<>(formService.updateForm(form), HttpStatus.OK);
	}

	@DeleteMapping("/forms/{id}")
	public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
		formService.deleteForm(id);
		return ResponseEntity.noContent().build();
//		catch (Exception e) {
//      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
	}
}
