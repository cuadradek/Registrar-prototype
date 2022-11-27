package cz.metacentrum.registrar.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

@Entity
public class Form {
	public enum FormState { SUBMITTED, VERIFIED, APPROVED, REJECTED }

	public enum FormType { INITIAL, EXTENSION }

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private UUID idmObject;

	@Column
	private UUID idmFormManagersGroup;

	@Column
	private String name;

	@Column
	private String redirectUrl;

	@ManyToMany
	@JoinTable(name = "form_redirect_form", joinColumns=@JoinColumn(name = "form_id"), inverseJoinColumns=@JoinColumn(name = "redirect_form_id"))
	private List<Form> redirectForms;

	@ManyToMany
	@JoinTable(name = "form_autosend_form", joinColumns=@JoinColumn(name = "form_id"), inverseJoinColumns=@JoinColumn(name = "autosend_form_id"))
	private List<Form> autosendForms;

	@Column
	private boolean canBeResubmitted;

	@Column
	private boolean autoApprove;

	@ManyToMany
	@JoinTable(name="form_nested_form", joinColumns=@JoinColumn(name="form_id"), inverseJoinColumns=@JoinColumn(name="nested_form_id"))
	private List<Form> nestedForms;

	@OneToMany(mappedBy = "form")
	private List<ApprovalGroup> approvalGroup;

	@OneToMany(mappedBy = "form")
	private List<AssignedFormModule> assignedModules;

	public Form() {
	}

	public Form(Long id, UUID idmObject, UUID idmFormManagersGroup, String name, String redirectUrl, List<Form> redirectForms, List<Form> autosendForms, boolean canBeResubmitted, boolean autoApprove, List<Form> nestedForms, List<ApprovalGroup> approvalGroup, List<AssignedFormModule> assignedModules) {
		this.id = id;
		this.idmObject = idmObject;
		this.idmFormManagersGroup = idmFormManagersGroup;
		this.name = name;
		this.redirectUrl = redirectUrl;
		this.redirectForms = redirectForms;
		this.autosendForms = autosendForms;
		this.canBeResubmitted = canBeResubmitted;
		this.autoApprove = autoApprove;
		this.nestedForms = nestedForms;
		this.approvalGroup = approvalGroup;
		this.assignedModules = assignedModules;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public List<Form> getRedirectForms() {
		return redirectForms;
	}

	public void setRedirectForms(List<Form> redirectForms) {
		this.redirectForms = redirectForms;
	}

	public List<Form> getAutosendForms() {
		return autosendForms;
	}

	public void setAutosendForms(List<Form> autosendForms) {
		this.autosendForms = autosendForms;
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

	public List<Form> getNestedForms() {
		return nestedForms;
	}

	public void setNestedForms(List<Form> nestedForms) {
		this.nestedForms = nestedForms;
	}

	public List<ApprovalGroup> getApprovalGroup() {
		return approvalGroup;
	}

	public void setApprovalGroup(List<ApprovalGroup> approvalGroup) {
		this.approvalGroup = approvalGroup;
	}

	public List<AssignedFormModule> getAssignedModules() {
		return assignedModules;
	}

	public void setAssignedModules(List<AssignedFormModule> assignedModules) {
		this.assignedModules = assignedModules;
	}
}
