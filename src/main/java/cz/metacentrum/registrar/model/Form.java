package cz.metacentrum.registrar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Form {

	public enum FormType { INITIAL, EXTENSION }

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private UUID iamObject;

	@Column
	private UUID iamFormManagersGroup;

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
}
