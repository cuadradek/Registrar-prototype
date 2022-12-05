package cz.metacentrum.registrar.rest;

import cz.metacentrum.registrar.persistence.entity.ApprovalGroup;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.service.FormService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Configuration
class InitialData {

	@Bean
	CommandLineRunner initDatabase(FormService formService) {

		return args -> {
			Form form = new Form(null, UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"), UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"),
					"My First Form", null, null, null, false, false, null,
					List.of(new ApprovalGroup(null, 0, false, 1, UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"))),
					Collections.emptyList());
			Form form1 = formService.createForm(form);
			formService.createForm(form);


			FormItem formItem = new FormItem(null, form1, "login", 0, true, false, FormItem.Type.TEXTFIELD,
					false, null, "user:login", "user:login", null,
					List.of(Form.FormType.INITIAL, Form.FormType.EXTENSION),
					null, null, FormItem.Disabled.NEVER, FormItem.Hidden.NEVER, false, null);
			formService.createFormItems(1L, List.of(formItem));
		};
	}
}
