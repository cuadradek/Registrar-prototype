package cz.metacentrum.registrar.rest.controller.dto;

import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.Form;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedFormSimpleDto {

	private Long id;

	private Long formId;

	private String formName;

	private Form.FormType formType;

	private Form.FormState formState;

	private AssignedFlowForm.FlowType flowType;

	private String submissionSubmitterId;
	private String submissionSubmitterName;
	private LocalDateTime submissionTimestamp;
}
