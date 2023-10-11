package cz.metacentrum.registrar.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormItem {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
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
