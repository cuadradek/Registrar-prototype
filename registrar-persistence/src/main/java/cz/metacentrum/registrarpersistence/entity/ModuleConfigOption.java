package cz.metacentrum.registrarpersistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ModuleConfigOption {
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
