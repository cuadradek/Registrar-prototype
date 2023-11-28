package cz.metacentrum.registrar.rest.controller.dto;

import cz.metacentrum.registrar.persistence.entity.Approval;
import lombok.Data;

@Data
public class CreateApprovalDto {

	private Long submittedFormId;
	private String message;
	private Approval.Decision decision;

}
