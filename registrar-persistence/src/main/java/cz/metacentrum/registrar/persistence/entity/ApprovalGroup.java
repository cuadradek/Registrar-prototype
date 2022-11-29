package cz.metacentrum.registrar.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class ApprovalGroup {
	@Id
	@GeneratedValue
	private Long id;

	@Column
	private int level;

	@Column
	private boolean mfaRequired;

	@Column
	private int minApprovals;

	@Column
	private UUID idmGroup;

//	@ManyToOne
//	@JoinColumn(name = "form_id")
//	@JsonIgnore // needed jackson dependency
//	private Form form;

	public ApprovalGroup() {
	}

	public ApprovalGroup(Long id, int level, boolean mfaRequired, int minApprovals, UUID idmGroup) {
		this.id = id;
		this.level = level;
		this.mfaRequired = mfaRequired;
		this.minApprovals = minApprovals;
		this.idmGroup = idmGroup;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isMfaRequired() {
		return mfaRequired;
	}

	public void setMfaRequired(boolean mfaRequired) {
		this.mfaRequired = mfaRequired;
	}

	public int getMinApprovals() {
		return minApprovals;
	}

	public void setMinApprovals(int minApprovals) {
		this.minApprovals = minApprovals;
	}

	public UUID getIdmGroup() {
		return idmGroup;
	}

	public void setIdmGroup(UUID idmGroup) {
		this.idmGroup = idmGroup;
	}
}
