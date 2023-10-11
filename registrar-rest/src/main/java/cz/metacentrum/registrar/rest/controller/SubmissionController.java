package cz.metacentrum.registrar.rest.controller;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.persistence.entity.FormItemData;
import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import cz.metacentrum.registrar.rest.config.RegistrarPrincipal;
import cz.metacentrum.registrar.rest.controller.dto.FormItemDataDto;
import cz.metacentrum.registrar.rest.controller.dto.SubmissionDto;
import cz.metacentrum.registrar.rest.controller.dto.SubmittedFormDto;
import cz.metacentrum.registrar.service.FormNotFoundException;
import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.service.SubmissionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/api")
public class SubmissionController {

	private final SubmissionService submissionService;
	private final FormService formService;
	private final ModelMapper modelMapper;

	@Autowired
	public SubmissionController(SubmissionService submissionService, FormService formService, ModelMapper modelMapper) {
		this.submissionService = submissionService;
		this.formService = formService;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/submissions/{id}")
	public ResponseEntity<SubmissionDto> getSubmissionById(@PathVariable Long id) {
		Optional<Submission> submission = submissionService.findSubmissionById(id);
		return submission
				.map(s -> new ResponseEntity<>(convertToDto(s), HttpStatus.OK))
				.orElseThrow(() -> new FormNotFoundException(id));
	}

	@GetMapping("/submitted-forms/{id}")
	public ResponseEntity<SubmittedFormDto> getSubmittedFormById(@PathVariable Long id) {
		Optional<SubmittedForm> submittedForm = submissionService.findSubmittedFormById(id);
		return submittedForm
				.map(s -> new ResponseEntity<>(convertToDto(s), HttpStatus.OK))
				.orElseThrow(() -> new FormNotFoundException(id));
	}

//	// TODO better generate / load endpoint
//	// TODO endpoint for callers submissions - is ?submittedby=xy enough?
//	@GetMapping("/submissions")
//	ResponseEntity<List<SubmissionDto>> getSubmissions(@RequestParam Long formId,
//													   @RequestParam(required = false) Form.FormState state,
//													   @RequestParam(required = false) Boolean generate) {
//		if (generate != null) {
//			return new ResponseEntity<>(List.of(convertToDto(submissionService.loadSubmission(formId))), HttpStatus.OK);
//		}
//
//		Form form = formService.getFormById(formId)
//				.orElseThrow(() -> new FormNotFoundException(formId));
//		if (state == null) {
//			return new ResponseEntity<>(submissionService.findSubmittedFormsByForm(form)
//					.stream()
//					.map(this::convertToDto)
//					.toList(),
//					HttpStatus.OK);
//		} else {
//			return new ResponseEntity<>(submissionService.findSubmittedFormsByFormAndState(form, state)
//					.stream()
//					.map(this::convertToDto)
//					.toList(),
//					HttpStatus.OK);
//		}
//	}
//
	@PostMapping("/submissions")
	public ResponseEntity<SubmissionDto> createSubmission(final @RequestBody @Validated SubmissionDto submissionDto,
														  @AuthenticationPrincipal RegistrarPrincipal principal) {
		if (principal != null) {
			submissionDto.setSubmittedById(principal.getName());
			submissionDto.setSubmittedByName(principal.getName());
		}
		Submission submission = submissionService.createSubmission(convertToEntity(submissionDto));
		return new ResponseEntity<>(convertToDto(submission), HttpStatus.CREATED);
	}

	@PutMapping("/submitted-forms/{id}/approve")
	public ResponseEntity<SubmittedFormDto> approveSubmittedForm(final @PathVariable Long id) {
		SubmittedForm submittedForm = submissionService.approveSubmittedForm(id);
		return new ResponseEntity<>(convertToDto(submittedForm), HttpStatus.CREATED);
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
