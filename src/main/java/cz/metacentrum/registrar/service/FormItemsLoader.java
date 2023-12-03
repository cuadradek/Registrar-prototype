package cz.metacentrum.registrar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import cz.metacentrum.registrar.model.FormItem;
import cz.metacentrum.registrar.service.iam.FormItemModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FormItemsLoader {

	@Value("${registrar.idm.form-items.config:}")
	private String configPath;

	private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	private Map<String, FormItemModule> formItemModules;

	@PostConstruct
	private void loadFormModules() throws IOException {
		if (StringUtils.isEmpty(configPath)) {
			formItemModules = new HashMap<>();
			log.warn("No IAM form item modules were loaded, because 'registrar.idm.form-items.config' is not configured!");
			return;
		}
		TypeReference<HashMap<String,FormItemModule>> typeRef
				= new TypeReference<>() {};
		try {
			formItemModules = mapper.readValue(new File(configPath), typeRef);
		} catch (IOException e) {
			log.error("Error while loading additional form modules", e);
			throw e;
		}
	}

	public Map<String, FormItemModule> getFormItemModules() {
		return Collections.unmodifiableMap(formItemModules);
	}

	public void validateItem(FormItem item) {
		String destAttribute = item.getIamDestinationAttribute();
		var itemModule = formItemModules.get(destAttribute);
		if (itemModule == null) {
			throw new IllegalArgumentException("Unsupported form item: " + destAttribute);
		}
		nullOrContains(itemModule.getIamSourceAttributes(), item.getIamSourceAttribute());
		nullOrContains(itemModule.getSourceIdentityAttributes(), item.getSourceIdentityAttribute());
		nullOrContains(itemModule.getItemTypes(), item.getType());
		nullOrContains(itemModule.getUpdatable(), item.isUpdatable());
		nullOrContains(itemModule.getRegex(), item.getRegex());
		nullOrContains(itemModule.getDisabled(), item.getDisabled());
		nullOrContains(itemModule.getHidden(), item.getHidden());
	}

	private <V> void nullOrContains(@Nullable Collection<V> collection, @Nullable V value) {
		if (collection != null && !collection.contains(value)) {
			throw new IllegalArgumentException("Value '"  + value + "' is not allowed.");
		}
	}
}