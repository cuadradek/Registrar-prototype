package cz.metacentrum.registrar.dto;

import cz.metacentrum.registrar.model.Approval;
import lombok.Data;

@Data
public class CreateApprovalDto {

	private Long submittedFormId;
	private String message;
	private Approval.Decision decision;

}
