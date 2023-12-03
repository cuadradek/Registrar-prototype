package cz.metacentrum.registrar.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
	private String submitterId;

	@Column
	private String submitterName;

	@Column
	private Integer originalIdentityLoa;

	@Column
	private String originalIdentityIdentifier;

	@Column
	private String originalIdentityIssuer;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="submission_identity_attributes")
	@Fetch(FetchMode.SUBSELECT)
	@MapKeyColumn(name = "attribute_name")
	@Column(name = "attribute_value")
	private Map<String, String> identityAttributes;

	@Column
	private LocalDateTime timestamp;

	@Transient
	private List<Identity> similarUsers;
}
