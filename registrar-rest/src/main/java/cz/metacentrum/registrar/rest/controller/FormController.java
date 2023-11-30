package cz.metacentrum.registrar.rest.controller;

import cz.metacentrum.registrar.persistence.entity.ApprovalGroup;
import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.rest.controller.dto.AssignedFlowFormDto;
import cz.metacentrum.registrar.rest.controller.dto.ExceptionResponse;
import cz.metacentrum.registrar.rest.controller.dto.FormDto;
import cz.metacentrum.registrar.service.FormNotFoundException;
import cz.metacentrum.registrar.service.FormService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@OpenAPIDefinition(
		info = @Info(title = "Registrar application"),
		servers = @Server(description = "my server", url = "{scheme}://{server}:{port}", variables = {
				@ServerVariable(name = "scheme", allowableValues = {"http", "https"}, defaultValue = "http"),
				@ServerVariable(name = "server", defaultValue = "localhost"),
				@ServerVariable(name = "port", defaultValue = "8080"),
		})
)
@Tag(name = "Form service", description = "endpoints for managing forms")
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class FormController {

	private final FormService formService;
	private final ModelMapper modelMapper;

	@Autowired
	public FormController(FormService formService, ModelMapper modelMapper) {
		this.formService = formService;
		this.modelMapper = modelMapper;
	}

	@Operation(summary = "Get all forms")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200")
	})
	@GetMapping("/forms")
	public List<FormDto> getAllForms() {
		return formService.getAllForms().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Operation(summary = "Get form by its id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200"),
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@PostAuthorize(
			"@permissionService.hasRole(#id, 'FORM_MANAGER')" +
			"|| @permissionService.hasRole(#id, 'FORM_APPROVER')" +
			"|| @permissionService.isObjectRightHolder(#returnObject)"
	)
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

	@GetMapping("/forms/{id}/modules")
	public List<AssignedFormModule> getAssignedModules(final @PathVariable Long id) {
		return formService.getAssignedModules(id);
	}

	@PutMapping("/forms/{id}/modules")
	public List<AssignedFormModule> setAssignedModules(final @PathVariable Long id,
													   final @RequestBody @Validated List<AssignedFormModule> modules) {
		return formService.setAssignedModules(id, modules);
	}

	@GetMapping("/forms/{id}/approval-groups")
	public List<ApprovalGroup> getApprovalGroups(final @PathVariable Long id) {
		return formService.getApprovalGroups(id);
	}

	@PutMapping("/forms/{id}/approval-groups")
	public List<ApprovalGroup> setApprovalGroups(final @PathVariable Long id,
												 final @RequestBody @Validated List<ApprovalGroup> groups) {
		return formService.setApprovalGroups(id, groups);
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
		return modelMapper.map(assignedFlowForm, AssignedFlowFormDto.class);
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
