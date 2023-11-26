package cz.metacentrum.registrar.rest.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormItemDataDto {

	@Nullable
	private Long id;

	private Long formItemId;

	private String value;

	private String assuranceLevel;
}
