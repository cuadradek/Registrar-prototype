package cz.metacentrum.registrar.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.lang.Nullable;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Arrays;
import java.util.List;

@Entity
public class FormItem {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "form_id")
	@JsonIgnore
	private Form form;

	@Column
	private String shortname;

	@Column
	private Integer ordnum;

	@Column
	private boolean required = false;

	@Column
	private boolean updatable = false;

	@Enumerated(EnumType.STRING) // possibly custom type for postgres enums
	private Type type = Type.TEXTFIELD;

	@Column
	private boolean preferFederationAttribute;

	@Column
	@Nullable
	private String federationAttribute;

	@Column
	@Nullable
	private String idmSourceAttribute;

	@Column
	@Nullable
	private String idmDestinationAttribute;

	@Column
	@Nullable
	private String regex;

	@Enumerated(EnumType.STRING)
	@ElementCollection
	@CollectionTable(name = "form_item_form_types", joinColumns = @JoinColumn(name = "item_id"))
	private List<Form.FormType> formTypes = Arrays.asList(Form.FormType.INITIAL, Form.FormType.EXTENSION);

	@Column
	@Nullable
	private Integer hiddenDependencyItemId;

	@Column
	@Nullable
	private Integer disabledDependencyItemId;

	@Enumerated(EnumType.STRING)
	private Disabled disabled = Disabled.NEVER;

	@Enumerated(EnumType.STRING)
	private Hidden hidden = Hidden.NEVER;

	@Column
	private boolean useInTemplate;

	@Column
	@Nullable
	private Integer templateDependencyItemId;

	public FormItem() {
	}

	public FormItem(Long id, Form form, String shortname, Integer ordnum, boolean required, boolean updatable, Type type, boolean preferFederationAttribute, String federationAttribute, String idmSourceAttribute, String idmDestinationAttribute, String regex, List<Form.FormType> formTypes, Integer hiddenDependencyItemId, Integer disabledDependencyItemId, Disabled disabled, Hidden hidden, boolean useInTemplate, Integer templateDependencyItemId) {
		this.id = id;
		this.form = form;
		this.shortname = shortname;
		this.ordnum = ordnum;
		this.required = required;
		this.updatable = updatable;
		this.type = type;
		this.preferFederationAttribute = preferFederationAttribute;
		this.federationAttribute = federationAttribute;
		this.idmSourceAttribute = idmSourceAttribute;
		this.idmDestinationAttribute = idmDestinationAttribute;
		this.regex = regex;
		this.formTypes = formTypes;
		this.hiddenDependencyItemId = hiddenDependencyItemId;
		this.disabledDependencyItemId = disabledDependencyItemId;
		this.disabled = disabled;
		this.hidden = hidden;
		this.useInTemplate = useInTemplate;
		this.templateDependencyItemId = templateDependencyItemId;
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

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public Integer getOrdnum() {
		return ordnum;
	}

	public void setOrdnum(Integer ordnum) {
		this.ordnum = ordnum;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isUpdatable() {
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public boolean isPreferFederationAttribute() {
		return preferFederationAttribute;
	}

	public void setPreferFederationAttribute(boolean preferFederationAttribute) {
		this.preferFederationAttribute = preferFederationAttribute;
	}

	public String getFederationAttribute() {
		return federationAttribute;
	}

	public void setFederationAttribute(String federationAttribute) {
		this.federationAttribute = federationAttribute;
	}

	public String getIdmSourceAttribute() {
		return idmSourceAttribute;
	}

	public void setIdmSourceAttribute(String idmSourceAttribute) {
		this.idmSourceAttribute = idmSourceAttribute;
	}

	public String getIdmDestinationAttribute() {
		return idmDestinationAttribute;
	}

	public void setIdmDestinationAttribute(String idmDestinationAttribute) {
		this.idmDestinationAttribute = idmDestinationAttribute;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public List<Form.FormType> getFormTypes() {
		return formTypes;
	}

	public void setFormTypes(List<Form.FormType> formTypes) {
		this.formTypes = formTypes;
	}

	public Integer getHiddenDependencyItemId() {
		return hiddenDependencyItemId;
	}

	public void setHiddenDependencyItemId(Integer hiddenDependencyItemId) {
		this.hiddenDependencyItemId = hiddenDependencyItemId;
	}

	public Integer getDisabledDependencyItemId() {
		return disabledDependencyItemId;
	}

	public void setDisabledDependencyItemId(Integer disabledDependencyItemId) {
		this.disabledDependencyItemId = disabledDependencyItemId;
	}

	public Disabled getDisabled() {
		return disabled;
	}

	public void setDisabled(Disabled disabled) {
		this.disabled = disabled;
	}

	public Hidden getHidden() {
		return hidden;
	}

	public void setHidden(Hidden hidden) {
		this.hidden = hidden;
	}

	public boolean isUseInTemplate() {
		return useInTemplate;
	}

	public void setUseInTemplate(boolean useInTemplate) {
		this.useInTemplate = useInTemplate;
	}

	public Integer getTemplateDependencyItemId() {
		return templateDependencyItemId;
	}

	public void setTemplateDependencyItemId(Integer templateDependencyItemId) {
		this.templateDependencyItemId = templateDependencyItemId;
	}

	/**
	 * Enumeration for types of application form items. For example text fields, checkboxes and so on.
	 */
	public enum Type {
		/**
		 * For inserting arbitrary HTML text into the form.
		 */
		HTML_COMMENT,
		/**
		 * For password, which needs to be typed twice.
		 */
		PASSWORD,
		/**
		 * For an email address that must be validated by sending an email with a URL.
		 */
		VALIDATED_EMAIL,
		/**
		 * Standard HTML text field.
		 */
		TEXTFIELD,
		USERNAME
	}

	public enum Hidden {
		NEVER,
		ALWAYS,
		IF_PREFILLED,
		IF_EMPTY
	}

	public enum Disabled {
		NEVER,
		ALWAYS,
		IF_PREFILLED,
		IF_EMPTY
	}

}
