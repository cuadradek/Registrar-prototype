package cz.metacentrum.registrar.dto;

import cz.metacentrum.registrar.model.AssignedFlowForm;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormState;
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

	private FormState formState;

	private AssignedFlowForm.FlowType flowType;

	private String submissionSubmitterId;
	private String submissionSubmitterName;
	private String submissionOriginalIdentityIdentifier;
	private String submissionOriginalIdentityIssuer;
	private LocalDateTime submissionTimestamp;
}
