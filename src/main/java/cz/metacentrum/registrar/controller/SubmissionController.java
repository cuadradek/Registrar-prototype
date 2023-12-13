package cz.metacentrum.registrar.controller;

import cz.metacentrum.registrar.dto.ExceptionResponse;
import cz.metacentrum.registrar.exception.SubmissionNotFoundException;
import cz.metacentrum.registrar.exception.SubmittedFormNotFoundException;
import cz.metacentrum.registrar.model.Approval;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormItem;
import cz.metacentrum.registrar.model.FormItemData;
import cz.metacentrum.registrar.model.FormState;
import cz.metacentrum.registrar.model.Submission;
import cz.metacentrum.registrar.model.SubmissionResult;
import cz.metacentrum.registrar.model.SubmittedForm;
import cz.metacentrum.registrar.dto.CreateApprovalDto;
import cz.metacentrum.registrar.dto.FormItemDataDto;
import cz.metacentrum.registrar.dto.SubmissionDto;
import cz.metacentrum.registrar.dto.SubmissionResultDto;
import cz.metacentrum.registrar.dto.SubmittedFormDto;
import cz.metacentrum.registrar.dto.SubmittedFormSimpleDto;
import cz.metacentrum.registrar.exception.ValidationException;
import cz.metacentrum.registrar.exception.FormNotFoundException;
import cz.metacentrum.registrar.security.PermissionService;
import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Submission service", description = "endpoints for submitting and approving forms")
@RestController
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
public class SubmissionController {

	private final SubmissionService submissionService;
	private final FormService formService;
	private final PermissionService permissionService; // TODO: auth rules not implemented yet
	private final ModelMapper modelMapper;

	@Autowired
	public SubmissionController(SubmissionService submissionService, FormService formService, PermissionService permissionService, ModelMapper modelMapper) {
		this.submissionService = submissionService;
		this.formService = formService;
		this.permissionService = permissionService;
		this.modelMapper = modelMapper;
	}

	@Operation(summary = "Gets a submission by its id.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Submission not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@GetMapping("/submissions/{id}")
	public SubmissionDto getSubmissionById(@PathVariable Long id) {
		Optional<Submission> submission = submissionService.findSubmissionById(id);
		return submission
				.map(this::convertToDto)
				.orElseThrow(() -> new SubmissionNotFoundException(id));
	}

	@Operation(summary = "Loads a form prepared for submission. One of the urlSuffix and formId parameters is required " +
			"to identify the requested form. The redirectUrl parameter is optional to redirect user to the URL after submission.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form with given urlSuffix or formId not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@GetMapping("/submissions/load")
	public SubmissionDto loadForm(@RequestParam(required = false) String urlSuffix,
								  @RequestParam(required = false) Long formId,
								  @RequestParam(required = false) String redirectUrl) {
		if (urlSuffix == null && formId == null) {
			throw new ValidationException("urlSuffix or formId parameter has to be present!");
		}

		Form form = null;
		if (urlSuffix != null) {
			form = formService.getFormByUrlSuffix(urlSuffix).orElseThrow(() -> new FormNotFoundException(urlSuffix));
		}
		if (formId != null) {
			form = formService.getFormById(formId).orElseThrow(() -> new FormNotFoundException(formId));
		}

		return convertToDto(submissionService.loadSubmission(form, redirectUrl));
	}

	@Operation(summary = "Gets a submitted form by its id.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Submitted form not found",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@GetMapping("/submitted-forms/{id}")
	public SubmittedFormDto getSubmittedFormById(@PathVariable Long id) {
		Optional<SubmittedForm> submittedForm = submissionService.findSubmittedFormById(id);
		return submittedForm
				.map(this::convertToDto)
				.orElseThrow(() -> new SubmittedFormNotFoundException(id));
	}

	@Operation(summary = "Finds submitted forms by query parameters.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Form not found for {formId}",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@GetMapping("/submitted-forms")
	public List<SubmittedFormSimpleDto> getSubmittedForms(@RequestParam(required = false) String submittedBy,
														  @RequestParam(required = false) Long formId,
														  @RequestParam(required = false) FormState state) {
		if (submittedBy == null && formId == null) {
			throw new ValidationException("submittedBy or formId parameter has to be present!");
		}

		List<SubmittedForm> submittedForms = new ArrayList<>();
		if (submittedBy != null) {
			submittedForms = submissionService.getSubmittedFormsBySubmitterId(submittedBy);
		}
		if (formId != null) {
			Form form = formService.getFormById(formId).orElseThrow(() -> new FormNotFoundException(formId));
			if (state == null) {
				submittedForms = submissionService.findSubmittedFormsByForm(form);
			} else {
				submittedForms = submissionService.findSubmittedFormsByFormAndState(form, state);
			}
		}

		return submittedForms.stream()
				.map(s -> modelMapper.map(s, SubmittedFormSimpleDto.class))
				.collect(Collectors.toList());
	}

	@Operation(summary = "Creates a submission based on the request body.")
	@PostMapping("/submissions")
	public SubmissionResultDto createSubmission(final @RequestBody @Valid SubmissionDto submissionDto) {
		SubmissionResult result = submissionService.createSubmission(convertToEntity(submissionDto));
		SubmissionResultDto resultDto = modelMapper.map(result, SubmissionResultDto.class);
		resultDto.setSubmission(convertToDto(result.getSubmission()));
		if (result.getRedirectSubmission() != null) {
			resultDto.setRedirectSubmission(convertToDto(result.getRedirectSubmission()));
		}
		return resultDto;
	}

	@Operation(summary = "Creates an approval based on the request body.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "404", description = "Submitted form not found for 'submittedFormId' in the request body}",
					content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	})
	@PostMapping("/submitted-forms/approval")
	public SubmittedFormDto makeApprovalDecision(final @RequestBody @Valid CreateApprovalDto approvalDto) {
		Approval approval = modelMapper.map(approvalDto, Approval.class);
		SubmittedForm submittedForm = submissionService.findSubmittedFormById(approvalDto.getSubmittedFormId())
				.orElseThrow(() -> new SubmittedFormNotFoundException(approvalDto.getSubmittedFormId()));
		approval.setSubmittedForm(submittedForm);

		submittedForm = submissionService.createApproval(approval);
		return convertToDto(submittedForm);
	}

	private SubmissionDto convertToDto(Submission submission) {
		SubmissionDto submissionDto = modelMapper.map(submission, SubmissionDto.class);
		submissionDto.setSubmittedForms(submission.getSubmittedForms().stream()
				.map(s -> convertToDto(s, submission.getId()))
				.collect(Collectors.toList()));
		return submissionDto;
	}

	private SubmittedFormDto convertToDto(SubmittedForm submittedForm) {
		return convertToDto(submittedForm, submittedForm.getSubmission().getId());
	}

	private SubmittedFormDto convertToDto(SubmittedForm submittedForm, Long submissionId) {
		SubmittedFormDto submittedFormDto = modelMapper.map(submittedForm, SubmittedFormDto.class);
		submittedFormDto.setSubmissionId(submissionId);
		submittedFormDto.setFormId(submittedForm.getForm().getId());
		submittedFormDto.setFormData(
				submittedForm.getFormData().stream()
						.map(this::convertToDto)
						.collect(Collectors.toList()));
		return submittedFormDto;
	}

	private FormItemDataDto convertToDto(FormItemData itemData) {
		FormItemDataDto itemDataDto = modelMapper.map(itemData, FormItemDataDto.class);
		itemDataDto.setFormItemId(itemData.getFormItem().getId());
		return itemDataDto;
	}

	private Submission convertToEntity(SubmissionDto submissionDto) {
		Submission submission = modelMapper.map(submissionDto, Submission.class);
		List<SubmittedForm> submittedForms = submissionDto.getSubmittedForms().stream()
				.map(submittedForm -> convertToEntity(submittedForm, submission))
				.collect(Collectors.toList());
		submission.setSubmittedForms(submittedForms);
		return submission;
	}

	private FormItemData convertToEntity(FormItemDataDto formItemDataDto) {
		FormItemData formItemData = modelMapper.map(formItemDataDto, FormItemData.class);
		var formItem = new FormItem();
		formItem.setId(formItemDataDto.getFormItemId());
		formItemData.setFormItem(formItem);

		return formItemData;
	}

	private SubmittedForm convertToEntity(SubmittedFormDto submittedFormDto, Submission submission) {
		SubmittedForm submittedForm = modelMapper.map(submittedFormDto, SubmittedForm.class);
		submittedForm.setSubmission(submission);
		Form form = formService.getFormById(submittedFormDto.getFormId())
				.orElseThrow(() -> new FormNotFoundException(submittedFormDto.getFormId()));
		submittedForm.setForm(form);
		submittedForm.setFormData(
				submittedFormDto.getFormData().stream()
						.map(this::convertToEntity)
						.collect(Collectors.toList()));
		return submittedForm;
	}
}
