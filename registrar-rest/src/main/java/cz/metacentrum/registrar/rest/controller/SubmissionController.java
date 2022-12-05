package cz.metacentrum.registrar.rest.controller;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.rest.controller.dto.SubmissionDto;
import cz.metacentrum.registrar.service.FormNotFoundException;
import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.service.SubmissionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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
		Optional<Submission> submissionData = submissionService.findSubmissionById(id);
		return submissionData
				.map(form -> new ResponseEntity<>(convertToDto(submissionData.get()), HttpStatus.OK))
				.orElseThrow(() -> new FormNotFoundException(id));
	}

	// TODO better generate / load endpoint
	// TODO endpoint for callers submissions - is ?submittedby=xy enough?
	@GetMapping("/submissions")
	ResponseEntity<List<SubmissionDto>> getSubmissions(@RequestParam Long formId,
													   @RequestParam(required = false) Form.FormState state,
													   @RequestParam(required = false) Boolean generate) {
		if (generate != null) {
			return new ResponseEntity<>(List.of(convertToDto(submissionService.loadSubmission(formId))), HttpStatus.OK);
		}

		Form form = formService.getFormById(formId)
				.orElseThrow(() -> new FormNotFoundException(formId));
		if (state == null) {
			return new ResponseEntity<>(submissionService.findSubmissionsByForm(form)
					.stream()
					.map(this::convertToDto)
					.toList(),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<>(submissionService.findSubmissionsByFormAndState(form, state)
					.stream()
					.map(this::convertToDto)
					.toList(),
					HttpStatus.OK);
		}
	}

	@PostMapping("/submissions")
	public ResponseEntity<SubmissionDto> submitForm(final @RequestBody @Validated SubmissionDto submissionDto) {
		Submission submission = submissionService.createSubmission(convertToEntity(submissionDto));
		return new ResponseEntity<>(convertToDto(submission), HttpStatus.CREATED);
	}

	@PutMapping("/submissions/{id}/approve")
	public ResponseEntity<SubmissionDto> approveSubmission(final @PathVariable Long id) {
		Submission submission = submissionService.approveSubmission(id);
		return new ResponseEntity<>(convertToDto(submission), HttpStatus.CREATED);
	}

	private SubmissionDto convertToDto(Submission submission) {
		SubmissionDto submissionDto = modelMapper.map(submission, SubmissionDto.class);
		submissionDto.setFormId(submissionDto.getFormId());
		return submissionDto;
	}

	private Submission convertToEntity(SubmissionDto submissionDto) {
		Submission submission = modelMapper.map(submissionDto, Submission.class);
		Form form = formService.getFormById(submissionDto.getFormId())
				.orElseThrow(() -> new FormNotFoundException(submissionDto.getFormId()));
		submission.setForm(form);
		return submission;
	}
}
