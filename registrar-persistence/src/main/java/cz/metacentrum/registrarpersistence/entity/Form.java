package cz.metacentrum.registrarpersistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@GeneratedValue(strategy= GenerationType.IDENTITY)
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

}
