package cz.metacentrum.registrar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalGroup implements Comparable<ApprovalGroup> {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "form_id")
	@JsonIgnore
	private Form form;

	@Column
	private int level;

	@Column
	private boolean mfaRequired;

	@Column
	private int minApprovals;

	@Column
	private UUID iamGroup;

	@Override
	public int compareTo(ApprovalGroup o) {
		return Integer.compare(level, o.level);
	}
}
