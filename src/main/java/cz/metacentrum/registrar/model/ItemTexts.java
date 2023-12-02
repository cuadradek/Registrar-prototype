package cz.metacentrum.registrar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
	private Locale locale;
	private String label;
	private String options;
	private String help;
	private String errorMessage;
}
