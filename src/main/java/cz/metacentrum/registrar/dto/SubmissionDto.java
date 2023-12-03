package cz.metacentrum.registrar.dto;

import cz.metacentrum.registrar.model.Identity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionDto {

	@Nullable
	private Long id;

	@NotEmpty
	private List<SubmittedFormDto> submittedForms;

	@Nullable
	private String submitterId;

	@Nullable
	private String submitterName;

	@Nullable
	private Integer originalIdentityLoa;

	@Nullable
	private String originalIdentityIdentifier;

	@Nullable
	private String originalIdentityIssuer;

	@Nullable
	private Map<String, String> identityAttributes;

	@Nullable
	private LocalDateTime timestamp;

	@Nullable
	private List<Identity> similarUsers;
}
