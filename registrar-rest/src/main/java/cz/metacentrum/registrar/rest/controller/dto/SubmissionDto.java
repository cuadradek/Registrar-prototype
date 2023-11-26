package cz.metacentrum.registrar.rest.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionDto {

	@Nullable
	private Long id;

	@NotEmpty
	private List<SubmittedFormDto> submittedForms;

	@Nullable
	private String identitySourceName;

	private int identitySourceLoa;

	@Nullable
	private String submitterId;

	@Nullable
	private String submitterName;

	@Nullable
	private LocalDateTime timestamp;
}
