package cz.metacentrum.registrar.rest.controller;

import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.rest.controller.dto.AssignedFlowFormDto;
import cz.metacentrum.registrar.rest.controller.dto.FormDto;
import cz.metacentrum.registrar.rest.controller.dto.ShortFormDto;
import cz.metacentrum.registrar.service.FormNotFoundException;
import cz.metacentrum.registrar.service.FormService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/api")
public class FormController {

	private final FormService formService;
	private final ModelMapper modelMapper;

	@Autowired
	public FormController(FormService formService, ModelMapper modelMapper) {
		this.formService = formService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/forms")
	public List<ShortFormDto> getAllForms() {
		return formService.getAllForms().stream()
				.map(f -> convertToDto(f, ShortFormDto.class))
				.collect(Collectors.toList());
	}

	@GetMapping("/forms/{id}")
	public FormDto getFormById(@PathVariable Long id) {
		Optional<Form> formData = formService.getFormById(id);
		return formData.map(this::convertToDto)
				.orElseThrow(() -> new FormNotFoundException(id));
	}

	@PostMapping("/forms")
	public ResponseEntity<FormDto> createForm(final @RequestBody @Validated FormDto formDTO) {
		Form form = formService.createForm(convertToEntity(formDTO));
		return new ResponseEntity<>(convertToDto(form), HttpStatus.CREATED);
	}

	@PutMapping("/forms")
	public FormDto updateForm(final @RequestBody @Validated FormDto formDTO) {
		Form form = formService.updateForm(convertToEntity(formDTO));
		return convertToDto(form);
	}

	@DeleteMapping("/forms/{id}")
	public void deleteForm(@PathVariable Long id) {
		formService.deleteForm(id);
	}

	@GetMapping("/forms/{id}/items")
	public List<FormItem> getFormItems(final @PathVariable Long id) {
		return formService.getFormItems(id);
	}

	@PutMapping("/forms/{id}/items")
	public List<FormItem> setFormItems(final @PathVariable Long id,
									   final @RequestBody @Validated List<FormItem> formItems) {
		return formService.setFormItems(id, formItems);
	}

	@GetMapping("/forms/{id}/flow-forms")
	public List<AssignedFlowFormDto> getAssignedFlowForms(final @PathVariable Long id) {
		return formService.getAssignedFlowForms(id).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@PutMapping("/forms/{id}/flow-forms")
	public List<AssignedFlowFormDto> setAssignedFlowForms(final @PathVariable Long id,
														  final @RequestBody @Validated List<AssignedFlowFormDto> flowForms) {
		var entities = flowForms.stream().map(this::convertToEntity).collect(Collectors.toList());
		return formService.setAssignedFlowForms(id, entities).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	private <T> T convertToDto(Form form, Class<T> tClass) {
		return modelMapper.map(form, tClass);
	}

	private FormDto convertToDto(Form form) {
		FormDto formDto = modelMapper.map(form, FormDto.class);
		return formDto;
	}

	private Set<Long> getFormsIds(List<Form> forms) {
		if (forms == null) return new HashSet<>();
		return forms
				.stream()
				.map(Form::getId)
				.collect(Collectors.toSet());
	}

	private AssignedFlowForm convertToEntity(AssignedFlowFormDto dto) {
		AssignedFlowForm assignedFlowForm = modelMapper.map(dto, AssignedFlowForm.class);
		Form form = formService.getFormById(dto.getFlowFormId())
				.orElseThrow(() -> new FormNotFoundException(dto.getFlowFormId()));
		assignedFlowForm.setFlowForm(form);
		return assignedFlowForm;
	}

	private AssignedFlowFormDto convertToDto(AssignedFlowForm assignedFlowForm) {
		AssignedFlowFormDto dto = modelMapper.map(assignedFlowForm, AssignedFlowFormDto.class);
		dto.setFlowFormId(assignedFlowForm.getFlowForm().getId());

		return dto;
	}

	private Form convertToEntity(FormDto formDto) {
		Form form = modelMapper.map(formDto, Form.class);

//		if (!CollectionUtils.isEmpty(formDto.getAssignedFlowForms())) {
//			form.setAssignedFlowForms(formDto.getAssignedFlowForms().stream()
//					.map(this::convertToEntity)
//					.collect(Collectors.toList()));
//		}

//		if (!CollectionUtils.isEmpty(formDto.getNestedFormsIds())) {
//			form.setNestedForms(formService.getFormsByIds(formDto.getNestedFormsIds()));
//		}
//		if (!CollectionUtils.isEmpty(formDto.getAutosendFormsIds())) {
//			form.setAutosendForms(formService.getFormsByIds(formDto.getAutosendFormsIds()));
//		}
//		if (!CollectionUtils.isEmpty(formDto.getRedirectFormsIds())) {
//			form.setRedirectForms(formService.getFormsByIds(formDto.getRedirectFormsIds()));
//		}

		return form;
	}
}
