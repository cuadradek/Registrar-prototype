package cz.metacentrum.registrar.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.util.List;

@Entity
public class AssignedFormModule implements Comparable<AssignedFormModule> {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "form_id")
	@JsonIgnore
	private Form form;

	@Column
	private String moduleName;

	@Transient // possibly implement converter between formModule <-> formModuleName
	private FormModule formModule;

	@OneToMany(mappedBy = "assignedModule")
	private List<ModuleConfigOption> configOption;

	@Column
	private int ordnum;

	public AssignedFormModule() {
	}

	public AssignedFormModule(Long id, Form form, String moduleName, FormModule formModule, List<ModuleConfigOption> configOption, int ordnum) {
		this.id = id;
		this.form = form;
		this.moduleName = moduleName;
		this.formModule = formModule;
		this.configOption = configOption;
		this.ordnum = ordnum;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public FormModule getFormModule() {
		return formModule;
	}

	public void setFormModule(FormModule formModule) {
		this.formModule = formModule;
	}

	public List<ModuleConfigOption> getConfigOption() {
		return configOption;
	}

	public void setConfigOption(List<ModuleConfigOption> configOption) {
		this.configOption = configOption;
	}

	public int getOrdnum() {
		return ordnum;
	}

	public void setOrdnum(int ordnum) {
		this.ordnum = ordnum;
	}

	@Override
	public int compareTo(AssignedFormModule o) {
		if (ordnum == o.ordnum) {
			return 0;
		}
		if (ordnum < o.ordnum) {
			return -1;
		}
		return 1;
	}
}
