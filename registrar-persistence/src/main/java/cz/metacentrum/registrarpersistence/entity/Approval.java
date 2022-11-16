package cz.metacentrum.registrarpersistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Approval {

	public enum Decision { APPROVED, REJECTED }

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private int level;

	@Column
	private boolean mfa;

	@Enumerated(EnumType.STRING)
	private Decision decision;

	@Column
	private String approvalBy;

	@Column
	private LocalDateTime timestamp;

	@Column
	private String message;

	public Approval() {
	}

	public Approval(Long id, int level, boolean mfa, Decision decision, String approvalBy, LocalDateTime timestamp, String message) {
		this.id = id;
		this.level = level;
		this.mfa = mfa;
		this.decision = decision;
		this.approvalBy = approvalBy;
		this.timestamp = timestamp;
		this.message = message;
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

	public boolean isMfa() {
		return mfa;
	}

	public void setMfa(boolean mfa) {
		this.mfa = mfa;
	}

	public Decision getDecision() {
		return decision;
	}

	public void setDecision(Decision decision) {
		this.decision = decision;
	}

	public String getApprovalBy() {
		return approvalBy;
	}

	public void setApprovalBy(String approvalBy) {
		this.approvalBy = approvalBy;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
