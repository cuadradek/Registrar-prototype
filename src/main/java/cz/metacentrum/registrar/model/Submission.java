package cz.metacentrum.registrar.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
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
	private String identitySourceName;

	@Column
	private int identitySourceLoa;

	@Column
	private String submitterId;

	@Column
	private String submitterName;

	@Column
	private LocalDateTime timestamp;

	@Transient
	private List<Identity> similarUsers;
}
