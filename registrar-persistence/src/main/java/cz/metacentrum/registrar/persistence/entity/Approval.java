package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Approval {

	public enum Decision { APPROVED, REJECTED, CHANGES_REQUESTED }

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private int level;

	@Column
	private boolean mfa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "submitted_form_id")
	private SubmittedForm submittedForm;

	@Enumerated(EnumType.STRING)
	private Decision decision;

	@Column
	private String approverId;

	@Column
	private String approverName;

	@Column
	private LocalDateTime timestamp;

	@Column
	private String message;
}
