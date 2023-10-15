package cz.metacentrum.registrar.rest.controller.dto;

import cz.metacentrum.registrar.persistence.entity.Form;
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
	private Form.FormState formState;

	private List<FormItemDataDto> formData;
}
