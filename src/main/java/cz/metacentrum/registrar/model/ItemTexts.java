package cz.metacentrum.registrar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemTexts {
	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	private Locale locale;
	@NotEmpty
	private String label;
	private String options;
	private String help;
	private String errorMessage;
}
