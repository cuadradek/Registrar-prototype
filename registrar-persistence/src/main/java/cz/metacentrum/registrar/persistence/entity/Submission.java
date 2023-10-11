package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Submission {

	@Id
	@GeneratedValue
	@Nullable
	private Long id;

	@NonNull
	@OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
	private List<SubmittedForm> submittedForms;

	@Column
	private String extSourceName;

	@Column
	private String extSourceType;

	@Column
	private int extSourceLoa;

	@Column
	private String submittedById;

	@Column
	private String submittedByName;

	@Column
	private LocalDateTime timestamp;
}
