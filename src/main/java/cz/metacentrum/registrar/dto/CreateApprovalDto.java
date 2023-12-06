package cz.metacentrum.registrar.dto;

import cz.metacentrum.registrar.model.Approval;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateApprovalDto {

	@NotNull
	private Long submittedFormId;
	private String message;
	@NotNull
	private Approval.Decision decision;

}
