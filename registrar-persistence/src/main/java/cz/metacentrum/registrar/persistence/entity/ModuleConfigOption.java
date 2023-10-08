package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleConfigOption implements Serializable {
	@ManyToOne
	@JoinColumn(name = "assigned_module_id")
	@Id
	private AssignedFormModule assignedModule;

	@Column
	@Id
	private String configOptionName;

	@Column
	private String configOptionValue;
}
