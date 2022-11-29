package cz.metacentrum.registrar.rest.controller;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.rest.controller.dto.FormDto;
import cz.metacentrum.registrar.service.FormNotFoundException;
import cz.metacentrum.registrar.service.FormService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping("/api")
public class FormController {

	private FormService formService;
	private ModelMapper modelMapper;

	@Autowired
	public FormController(FormService formService, ModelMapper modelMapper) {
		this.formService = formService;
		this.modelMapper = modelMapper;
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

//	@PostMapping("/forms")
//	public ResponseEntity<Form> createForm(final @RequestBody Form form) {
//		return new ResponseEntity<>(formService.createForm(form), HttpStatus.CREATED);
////		catch (Exception e) {
////      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
////    }
//	}

	// TODO: advice na zahrnutie message pri Validated chybe do response (momentalne sa vrati len 400 Bad request a nic detailnejsie)
	@PostMapping("/forms")
	public ResponseEntity<FormDto> createForm(final @RequestBody @Validated FormDto formDTO) {
		Form form = formService.createForm(convertToEntity(formDTO));
		return new ResponseEntity<>(convertToDto(form), HttpStatus.CREATED);
//		catch (Exception e) {
//      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
	}

	@PutMapping("/forms")
	public ResponseEntity<FormDto> updateForm(final @RequestBody @Validated FormDto formDTO) {
		Form form = formService.updateForm(convertToEntity(formDTO));
		return new ResponseEntity<>(convertToDto(form), HttpStatus.OK);
	}

	@DeleteMapping("/forms/{id}")
	public ResponseEntity<Void> deleteForm(@PathVariable Long id) {
		formService.deleteForm(id);
		return ResponseEntity.noContent().build();
//		catch (Exception e) {
//      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
	}

	private FormDto convertToDto(Form form) {
		FormDto formDto = modelMapper.map(form, FormDto.class);
		return formDto;
	}

	private Form convertToEntity(FormDto formDto) {
		Form form = modelMapper.map(formDto, Form.class);

		if (!CollectionUtils.isEmpty(formDto.getNestedFormsIds())) {
			form.setNestedForms(formService.getFormsByIds(formDto.getNestedFormsIds()));
		}
		if (!CollectionUtils.isEmpty(formDto.getAutosendFormsIds())) {
			form.setAutosendForms(formService.getFormsByIds(formDto.getAutosendFormsIds()));
		}
		if (!CollectionUtils.isEmpty(formDto.getRedirectFormsIds())) {
			form.setRedirectForms(formService.getFormsByIds(formDto.getRedirectFormsIds()));
		}

		return form;
	}
}
