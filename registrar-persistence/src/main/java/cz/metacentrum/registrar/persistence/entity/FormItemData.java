package cz.metacentrum.registrar.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class FormItemData {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "form_item_id")
	private FormItem formItem;

	@Column
	private String shortname;

	@Column(name = "item_value")
	private String value;

	@Column
	private String assuranceLevel;

	@Transient
	private String prefilledValue = "";

	@Transient
	private boolean generated;

	public FormItemData() {
	}

	public FormItemData(Long id, FormItem formItem, String shortname, String value, String assuranceLevel, String prefilledValue, boolean generated) {
		this.id = id;
		this.formItem = formItem;
		this.shortname = shortname;
		this.value = value;
		this.assuranceLevel = assuranceLevel;
		this.prefilledValue = prefilledValue;
		this.generated = generated;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FormItem getFormItem() {
		return formItem;
	}

	public void setFormItem(FormItem formItem) {
		this.formItem = formItem;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAssuranceLevel() {
		return assuranceLevel;
	}

	public void setAssuranceLevel(String assuranceLevel) {
		this.assuranceLevel = assuranceLevel;
	}

	public String getPrefilledValue() {
		return prefilledValue;
	}

	public void setPrefilledValue(String prefilledValue) {
		this.prefilledValue = prefilledValue;
	}

	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}
}
