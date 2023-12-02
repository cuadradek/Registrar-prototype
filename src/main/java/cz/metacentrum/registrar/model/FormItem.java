package cz.metacentrum.registrar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
import java.util.Locale;

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
	@OnDelete(action = OnDeleteAction.CASCADE) //this add ON DELETE CASCADE to table definition
	@JsonIgnore
	@ToString.Exclude //JsonIgnore and Exclude from toString to avoid n+1 query (toString used while logging which we don't need tbh)
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
	private boolean preferIdentityAttribute;

	@Column
	@Nullable
	private String prefilledStaticValue;

	@Column
	@Nullable
	private String sourceIdentityAttribute;

	@Column
	@Nullable
	private String iamSourceAttribute;

	@Column
	@Nullable
	private String iamDestinationAttribute;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "item_id")
	private List<ItemTexts> texts;

	@Column
	@Nullable
	private String regex;

	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "form_item_form_types")
	@JoinColumn(name = "item_id")
	@Fetch(FetchMode.SUBSELECT)
	@OnDelete(action = OnDeleteAction.CASCADE)
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
	private boolean isDeleted; //soft delete to keep form_item.id as foreign key in form_item_data

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
		IF_EMPTY,
		HAS_VALUE
	}

	public enum Disabled {
		NEVER,
		ALWAYS,
		IF_PREFILLED,
		IF_EMPTY,
		HAS_VALUE
	}

	public ItemTexts getTexts(Locale locale) {
		return texts.stream()
				.filter(t -> t.getLocale() == locale)
				.findFirst()
				.orElse(null);
	}

}
