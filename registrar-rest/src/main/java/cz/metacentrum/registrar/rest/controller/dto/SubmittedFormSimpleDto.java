package cz.metacentrum.registrar.rest.controller.dto;

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

	private String submissionSubmitterId;
	private String submissionSubmitterName;
	private LocalDateTime submissionTimestamp;
}
