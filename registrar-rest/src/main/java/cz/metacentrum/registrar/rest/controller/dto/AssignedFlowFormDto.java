package cz.metacentrum.registrar.rest.controller.dto;

import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.Form;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
public class AssignedFlowFormDto {

	@Nullable
	private Long id;

	@NotNull
	private AssignedFlowForm.FlowType flowType;

	@NotNull
	private Integer ordnum;

	@NotNull
	private Long flowFormId;

	@Nullable
	private String flowFormName;

	@NotEmpty
	private List<Form.FormType> ifFlowFormType;

	@NotEmpty
	private List<Form.FormType> ifMainFlowType;
}
