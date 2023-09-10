package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

@Entity
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

	public ModuleConfigOption() {
	}

	public ModuleConfigOption(AssignedFormModule assignedModule, String configOptionName, String configOptionValue) {
		this.assignedModule = assignedModule;
		this.configOptionName = configOptionName;
		this.configOptionValue = configOptionValue;
	}

	public AssignedFormModule getAssignedModule() {
		return assignedModule;
	}

	public void setAssignedModule(AssignedFormModule assignedModule) {
		this.assignedModule = assignedModule;
	}

	public String getConfigOptionName() {
		return configOptionName;
	}

	public void setConfigOptionName(String configOptionName) {
		this.configOptionName = configOptionName;
	}

	public String getConfigOptionValue() {
		return configOptionValue;
	}

	public void setConfigOptionValue(String configOptionValue) {
		this.configOptionValue = configOptionValue;
	}
}
