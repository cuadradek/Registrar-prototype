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

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private UUID idmObject;

	@Column
	private UUID idmFormManagersGroup;

	@Column
	private String name;

	@Column(unique = true)
	private String urlSuffix;

	@Column
	@Nullable
	private String redirectUrl;

	@Column
	private boolean canBeResubmitted;

	@Column
	private boolean autoApprove;

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true) //todo: maybe fetchtype subselect
	@JoinColumn(name = "form_id")
	private List<ApprovalGroup> approvalGroups = new ArrayList<>();

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true) //todo: maybe fetchtype subselect
	@JoinColumn(name = "form_id")
	private List<AssignedFormModule> assignedModules;
}
