package cz.metacentrum.registrar.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.Nullable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Form {
	public enum FormState { SUBMITTED, VERIFIED, APPROVED, REJECTED }

	public enum FormType { INITIAL, EXTENSION }

	public enum FlowType { PRE, AUTO, REDIRECT }

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
	@Nullable
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
	private List<Form> nestedForms = new ArrayList<>();

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(value = FetchMode.SUBSELECT)
//	@OneToMany(mappedBy = "form", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<ApprovalGroup> approvalGroups = new ArrayList<>();

	@OneToMany(mappedBy = "form", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<AssignedFormModule> assignedModules;
}
