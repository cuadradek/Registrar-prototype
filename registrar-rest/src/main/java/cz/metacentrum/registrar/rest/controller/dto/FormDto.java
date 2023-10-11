package cz.metacentrum.registrar.rest.controller.dto;

import cz.metacentrum.registrar.persistence.entity.ApprovalGroup;
import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import lombok.Data;
import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class FormDto {
	@Nullable
	Long id;
	@NotNull
	UUID idmObject;
	@NotNull
	UUID idmFormManagersGroup;
	@NotBlank
	@Size(min = 3, max = 30)
	String name;
	@NotBlank
	@Size(min = 3, max = 30)
	private String urlSuffix;
	@Nullable
	String redirectUrl;
	@Nullable
	Set<AssignedFlowFormDto> assignedFlowForms;
	@NotNull
	boolean canBeResubmitted;
	@NotNull
	boolean autoApprove;
	@Nullable
	Set<Long> nestedFormsIds;
	@Nullable
	List<ApprovalGroup> approvalGroups;
	@NotNull(message = "assignModules must be specified")
	List<AssignedFormModule> assignedModules;//TODO use AssignedFormModuleDTO instead
}
