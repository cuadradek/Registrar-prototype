package cz.metacentrum.registrar.dto;

import cz.metacentrum.registrar.model.AssignedFlowForm;
import cz.metacentrum.registrar.model.Form;
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
