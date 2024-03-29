package cz.metacentrum.registrar.service.iam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.metacentrum.registrar.model.FormItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * If a field is null, form manager can set any value.
 * If a field is not null, form manager can set only one of values from list.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class FormItemModule {

	private String id;
	private String displayName;
	private List<String> iamSourceAttributes;
	private List<String> sourceIdentityAttributes;
	private String iamDestinationAttribute;
	private List<String> prefilledStaticValue;
	private Map<Locale, List<String>> label;
	private List<FormItem.Type> itemTypes;
	private List<Boolean> updatable;
	private List<String> regex;
	private List<FormItem.Disabled> disabled;
	private List<FormItem.Hidden> hidden;
}
