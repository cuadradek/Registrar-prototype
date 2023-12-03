package cz.metacentrum.registrar.dto;

import cz.metacentrum.registrar.model.AssignedFlowForm;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormState;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedFormDto {

	@Nullable
	private Long id;

	@NotNull
	private int stepOrder;

	@NotNull
	private Long formId;

	@NotNull
	private Long submissionId;

	@Nullable
	private Form.FormType formType;

	@Nullable
	private FormState formState;

	private List<FormItemDataDto> formData;

	@Nullable
	private AssignedFlowForm.FlowType flowType;

	@Nullable
	private String redirectUrl;
}
