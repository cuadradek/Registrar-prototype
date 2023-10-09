package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleConfigOption implements Serializable {

	@Column
	private String configOptionName;

	@Column
	private String configOptionValue;
}
