package cz.metacentrum.registrar.service.formitems;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cz.metacentrum.registrar.persistence.entity.FormItem;
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
	private List<String> sourceIdmAttributes;
	private List<String> sourceFederationAttributes;
	private String destinationAttribute;
	private Map<Locale, List<String>> label;
	private List<FormItem.Type> itemTypes;
	private List<Boolean> updatable;
	private List<String> regex;
	private List<FormItem.Disabled> disabled;
	private List<FormItem.Hidden> hidden;

//	TODO labels
//  label:
//    - en:
//      - Login
//      - login
//      - Username
//  labelRegex:
//    - en: /login/i
//  includePolicy:
//    - usernamePolicy
//  help: any
//  error: any

}
