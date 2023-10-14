package cz.metacentrum.registrar.rest;

import cz.metacentrum.registrar.persistence.entity.ApprovalGroup;
import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.persistence.entity.Role;
import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
class InitialData {

	private static final String EINFRA_LOGIN = "urn:perun:user:attribute-def:def:login-namespace:einfra";

	@Bean
	CommandLineRunner initDatabase(FormService formService, RoleService roleService) {

		return args -> {
			Form form = new Form(null, UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"), UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"),
					"My First Form", "my-first-form", null, false, false,
					List.of(new ApprovalGroup(null, 0, false, 1, UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"))),
					null);
			AssignedFormModule module = new AssignedFormModule(null, "addToVo", Map.of("VO", "1"), 0);
			form.setAssignedModules(List.of(module));
			Form form1 = formService.createForm(form);

			FormItem formItem = new FormItem(null, form1, "login", 0, true, false, FormItem.Type.USERNAME,
					false, null, EINFRA_LOGIN, EINFRA_LOGIN, null,
					List.of(Form.FormType.INITIAL, Form.FormType.EXTENSION),
					null, null, FormItem.Disabled.NEVER, FormItem.Hidden.NEVER, false);
			formService.setFormItems(1L, List.of(formItem));

			var adminRole = new Role();
			adminRole.setName("ADMIN");
			adminRole.setAssignedUsers(List.of("114249895833464720724"));
			roleService.createRole(adminRole);
		};
	}
}
