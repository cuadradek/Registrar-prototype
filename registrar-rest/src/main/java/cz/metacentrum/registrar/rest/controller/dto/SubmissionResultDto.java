package cz.metacentrum.registrar.rest.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmissionResultDto {
	private SubmissionDto submission;
	private List<String> messages;
	private SubmissionDto redirectSubmission;
}
