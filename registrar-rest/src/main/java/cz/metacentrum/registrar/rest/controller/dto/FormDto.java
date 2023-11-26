package cz.metacentrum.registrar.rest.controller.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Data
public class FormDto {
	@Nullable
	private Long id;
	@NotNull
	private UUID iamObject;
	@NotNull
	private UUID iamFormManagersGroup;
	@NotBlank
	@Size(min = 3, max = 30)
	private String name;
	@NotBlank
	@Size(min = 3, max = 30)
	private String urlSuffix;
	@Nullable
	private String redirectUrl;
	@NotNull
	boolean canBeResubmitted;
	@NotNull
	boolean autoApprove;
}
