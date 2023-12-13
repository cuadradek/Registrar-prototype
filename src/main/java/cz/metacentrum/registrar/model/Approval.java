package cz.metacentrum.registrar.model;

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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
	@OnDelete(action = OnDeleteAction.CASCADE)
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
