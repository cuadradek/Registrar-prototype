package cz.metacentrum.registrar.controller;

import cz.metacentrum.registrar.model.ApprovalGroup;
import cz.metacentrum.registrar.model.AssignedFlowForm;
import cz.metacentrum.registrar.model.AssignedFormModule;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormItem;
import cz.metacentrum.registrar.security.PermissionService;
import cz.metacentrum.registrar.service.iam.FormModule;
import cz.metacentrum.registrar.dto.AssignedFlowFormDto;
import cz.metacentrum.registrar.dto.ExceptionResponse;
import cz.metacentrum.registrar.dto.FormDto;
import cz.metacentrum.registrar.exception.FormNotFoundException;
import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.service.iam.IamService;
import cz.metacentrum.registrar.security.RegistrarPrincipal;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		scheme = "bearer",
		in = SecuritySchemeIn.HEADER
)
@SecurityRequirement(name = "bearerAuth")
@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Invalid query parameters or request body",
				content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
		@ApiResponse(responseCode = "403", description = "Insufficient permission",
				content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
		@ApiResponse(responseCode = "500", description = "Internal error",
				content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
})
@Validated // necessary when request body is list of objects that need to be validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class FormController {

	private final FormService formService;
	private final ModelMapper modelMapper;
	private final IamService iamService;
	private final PermissionService permissionService;
	private final ApplicationContext context;
	private final Environment environment;

	@Autowired
	public FormController(FormService formService, ModelMapper modelMapper, IamService iamService, PermissionService permissionService, ApplicationContext context, Environment environment) {
		this.formService = formService;
		this.modelMapper = modelMapper;
		this.iamService = iamService;
		this.permissionService = permissionService;
		this.context = context;
		this.environment = environment;
	}

	@Operation(summary = "Gets all forms a caller is authorized for.")
	@GetMapping("/forms")
	public List<FormDto> getAllForms() {
		return formService.getAllForms().stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Operation(summary = "Gets a form by its id.")
	@ApiResponses(value = {
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

	@Operation(summary = "Create a form based on the provided data in the request body.")
	@PostMapping("/forms")
	public FormDto createForm(final @RequestBody @Valid FormDto formDTO,
							  @AuthenticationPrincipal RegistrarPrincipal principal) {
		if (performAuthorization() && formDTO.getIamObject() == null && !iamService.canCreateForm(principal.getId())) {
			throw new AccessDeniedException("Not allowed to create form!");
		}
		if (performAuthorization() && formDTO.getIamObject() != null && !iamService.isObjectRightHolder(principal.getId(), formDTO.getIamObject())) {
			throw new AccessDeniedException("Not allowed to create form!");
		}

		Form form = formService.createForm(convertToEntity(formDTO));
		return convertToDto(form);
	}

	@Operation(summary = "Update a form based on the provided data in the request body.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@PutMapping("/forms")
	public FormDto updateForm(final @RequestBody @Valid FormDto formDTO) {
		Form form = getFormOrElseThrow(formDTO.getId());
		if (performAuthorization() &&
				(!permissionService.hasRole(formDTO.getId(), "FORM_MANAGER")
						|| !permissionService.isObjectRightHolder(Optional.of(form)))) {
			throw new AccessDeniedException("Not allowed to update form: " + form.getId());
		}
		form = formService.updateForm(convertToEntity(formDTO));
		return convertToDto(form);
	}

	@Operation(summary = "Delete a form identified by the specified {id}.")
	@DeleteMapping("/forms/{id}")
	public void deleteForm(@PathVariable Long id) {
		Form form = getFormOrElseThrow(id);
		if (performAuthorization() &&
				(!permissionService.hasRole(id, "FORM_MANAGER")
						|| !permissionService.isObjectRightHolder(Optional.of(form)))) {
			throw new AccessDeniedException("Not allowed to delete form: " + form.getId());
		}
		formService.deleteForm(id);
	}

	@Operation(summary = "Gets form items for a form identified by the specified {id}.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@GetMapping("/forms/{id}/items")
	public List<FormItem> getFormItems(final @PathVariable Long id) {
		Form form = getFormOrElseThrow(id);
		if (performAuthorization() &&
				(!permissionService.hasRole(id, "FORM_MANAGER")
						|| !permissionService.hasRole(id, "FORM_APPROVER")
						|| !permissionService.isObjectRightHolder(Optional.of(form)))) {
			throw new AccessDeniedException("Not allowed to get form items for form: " + form.getId());
		}
		return formService.getFormItems(form);
	}

	private Form getFormOrElseThrow(@PathVariable Long id) {
		return formService.getFormById(id).orElseThrow(() -> new FormNotFoundException(id));
	}

	@Operation(summary = "Updates and/or creates given form items for a form identified by the specified {id}.)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@PutMapping("/forms/{id}/items")
	public List<FormItem> setFormItems(final @PathVariable Long id,
									   final @RequestBody @Valid List<FormItem> formItems) {
		Form form = getFormOrElseThrow(id);
		if (performAuthorization() &&
				(!permissionService.hasRole(id, "FORM_MANAGER")
				|| !permissionService.isObjectRightHolder(Optional.of(form)))) {
			throw new AccessDeniedException("Not allowed to update items for form: " + id);
		}
		return formService.setFormItems(form, formItems);
	}

	@Operation(summary = "Gets assigned flow forms for a form identified by the specified {id}.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@GetMapping("/forms/{id}/flow-forms")
	public List<AssignedFlowFormDto> getAssignedFlowForms(final @PathVariable Long id) {
		Form form = getFormOrElseThrow(id);
		if (performAuthorization() &&
				(!permissionService.hasRole(id, "FORM_MANAGER")
						|| !permissionService.hasRole(id, "FORM_APPROVER")
						|| !permissionService.isObjectRightHolder(Optional.of(form)))) {
			throw new AccessDeniedException("Not allowed to get flow forms for form: " + form.getId());
		}
		return formService.getAssignedFlowForms(form).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Operation(summary = "Updates, assigns, or removes given form flows for a form identified by the specified {id}. " +
			"If an already assigned flow is missing in the request body, it will be removed!")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@PutMapping("/forms/{id}/flow-forms")
	public List<AssignedFlowFormDto> setAssignedFlowForms(final @PathVariable Long id,
														  final @RequestBody @Valid List<AssignedFlowFormDto> flowForms) {
		Form form = getFormOrElseThrow(id);
		if (performAuthorization() &&
				(!permissionService.hasRole(id, "FORM_MANAGER")
						|| !permissionService.isObjectRightHolder(Optional.of(form)))) {
			throw new AccessDeniedException("Not allowed to update flow forms for form: " + form.getId());
		}
		var entities = flowForms.stream().map(this::convertToEntity).collect(Collectors.toList());
		return formService.setAssignedFlowForms(form, entities).stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	@Operation(summary = "Gets assigned form modules for a form identified by the specified {id}.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@GetMapping("/forms/{id}/modules")
	public List<AssignedFormModule> getAssignedModules(final @PathVariable Long id) {
		Form form = getFormOrElseThrow(id);
		if (performAuthorization() &&
				(!permissionService.hasRole(id, "FORM_MANAGER")
						|| !permissionService.isObjectRightHolder(Optional.of(form)))) {
			throw new AccessDeniedException("Not allowed to get form modules for form: " + form.getId());
		}
		return formService.getAssignedModules(form);
	}

	@Operation(summary = "Updates, assigns, or removes given modules for a form identified by the specified {id}. " +
			"If an already assigned module is missing in the request body, it will be removed!")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@PutMapping("/forms/{id}/modules")
	public List<AssignedFormModule> setAssignedModules(final @PathVariable Long id,
													   final @RequestBody @Valid List<AssignedFormModule> modules) {
		Form form = getFormOrElseThrow(id);
		if (performAuthorization() &&
				(!permissionService.hasRole(id, "FORM_MANAGER")
						|| !permissionService.isObjectRightHolder(Optional.of(form)))) {
			throw new AccessDeniedException("Not allowed to changed modules for form: " + form.getId());
		}
		if (performAuthorization()) {
			modules.forEach(m -> {
				setModule(m);
				if (!m.getFormModule().hasRightToAddToForm(form, m.getConfigOptions())) {
					throw new AccessDeniedException("You don't have rights to assign this module: " + m);
				}
			});
		}

		return formService.setAssignedModules(form, modules);
	}

	private AssignedFormModule setModule(AssignedFormModule assignedModule) {
		try {
			FormModule formModule = context.getBean(assignedModule.getModuleName(), FormModule.class);
			assignedModule.setFormModule(formModule);
			return assignedModule;
		} catch (BeansException ex) {
			throw new IllegalArgumentException("Non existing form module: " + assignedModule.getModuleName());
		}
	}

	@Operation(summary = "Gets approval groups for a form identified by the specified {id}.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@GetMapping("/forms/{id}/approval-groups")
	public List<ApprovalGroup> getApprovalGroups(final @PathVariable Long id) {
		Form form = getFormOrElseThrow(id);
		return formService.getApprovalGroups(form);
	}

	@Operation(summary = "Updates, assigns, or removes given approval groups for a form identified by the specified {id}. " +
			"If an already assigned group is missing in the request body, it will be removed!")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@PutMapping("/forms/{id}/approval-groups")
	public List<ApprovalGroup> setApprovalGroups(final @PathVariable Long id,
												 final @RequestBody @Valid List<ApprovalGroup> groups) {
		Form form = getFormOrElseThrow(id);
		return formService.setApprovalGroups(form, groups);
	}

	private FormDto convertToDto(Form form) {
		return modelMapper.map(form, FormDto.class);
	}

	private AssignedFlowForm convertToEntity(AssignedFlowFormDto dto) {
		AssignedFlowForm assignedFlowForm = modelMapper.map(dto, AssignedFlowForm.class);
		Form form = getFormOrElseThrow(dto.getFlowFormId());
		assignedFlowForm.setFlowForm(form);
		return assignedFlowForm;
	}

	private AssignedFlowFormDto convertToDto(AssignedFlowForm assignedFlowForm) {
		return modelMapper.map(assignedFlowForm, AssignedFlowFormDto.class);
	}

	private Form convertToEntity(FormDto formDto) {
		return modelMapper.map(formDto, Form.class);
	}

	private boolean performAuthorization() {
		return !Arrays.asList(environment.getActiveProfiles()).contains("local");
	}
}
