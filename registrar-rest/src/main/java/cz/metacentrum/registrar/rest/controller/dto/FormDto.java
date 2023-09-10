package cz.metacentrum.registrar.rest.controller.dto;

import cz.metacentrum.registrar.persistence.entity.ApprovalGroup;
import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
	@Nullable
	String redirectUrl;
	@Nullable
	Set<Long> redirectFormsIds; // TODO: maybe FormNameDTO(id, name) to have its name also or use HATEOAS link to the form
	@Nullable
	Set<Long> autosendFormsIds;
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

	@Nullable
	public Long getId() {
		return id;
	}

	public void setId(@Nullable Long id) {
		this.id = id;
	}

	public UUID getIdmObject() {
		return idmObject;
	}

	public void setIdmObject(UUID idmObject) {
		this.idmObject = idmObject;
	}

	public UUID getIdmFormManagersGroup() {
		return idmFormManagersGroup;
	}

	public void setIdmFormManagersGroup(UUID idmFormManagersGroup) {
		this.idmFormManagersGroup = idmFormManagersGroup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Nullable
	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(@Nullable String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	@Nullable
	public Set<Long> getRedirectFormsIds() {
		return redirectFormsIds;
	}

	public void setRedirectFormsIds(@Nullable Set<Long> redirectFormsIds) {
		this.redirectFormsIds = redirectFormsIds;
	}

	@Nullable
	public Set<Long> getAutosendFormsIds() {
		return autosendFormsIds;
	}

	public void setAutosendFormsIds(@Nullable Set<Long> autosendFormsIds) {
		this.autosendFormsIds = autosendFormsIds;
	}

	public boolean isCanBeResubmitted() {
		return canBeResubmitted;
	}

	public void setCanBeResubmitted(boolean canBeResubmitted) {
		this.canBeResubmitted = canBeResubmitted;
	}

	public boolean isAutoApprove() {
		return autoApprove;
	}

	public void setAutoApprove(boolean autoApprove) {
		this.autoApprove = autoApprove;
	}

	@Nullable
	public Set<Long> getNestedFormsIds() {
		return nestedFormsIds;
	}

	public void setNestedFormsIds(@Nullable Set<Long> nestedFormsIds) {
		this.nestedFormsIds = nestedFormsIds;
	}

	@Nullable
	public List<ApprovalGroup> getApprovalGroups() {
		return approvalGroups;
	}

	public void setApprovalGroups(@Nullable List<ApprovalGroup> approvalGroups) {
		this.approvalGroups = approvalGroups;
	}

	public List<AssignedFormModule> getAssignedModules() {
		return assignedModules;
	}

	public void setAssignedModules(List<AssignedFormModule> assignedModules) {
		this.assignedModules = assignedModules;
	}
}
