package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
