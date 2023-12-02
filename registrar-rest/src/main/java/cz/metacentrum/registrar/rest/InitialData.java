package cz.metacentrum.registrar.rest;

import cz.metacentrum.registrar.persistence.entity.ApprovalGroup;
import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.persistence.entity.ItemTexts;
import cz.metacentrum.registrar.persistence.entity.Role;
import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Configuration
class InitialData {

	private static final String EINFRA_LOGIN = "urn:perun:user:attribute-def:def:login-namespace:einfra";

	@Bean
	CommandLineRunner initDatabase(FormService formService, RoleService roleService) {

		return args -> {
			Form firstForm = createFirstForm(formService);
			Form secondForm = createSecondForm(formService);
			var flowForm = new AssignedFlowForm(null, AssignedFlowForm.FlowType.AUTO, 0, secondForm, firstForm,
					List.of(Form.FormType.INITIAL), List.of(Form.FormType.INITIAL));
			formService.setAssignedFlowForms(firstForm.getId(), List.of(flowForm));

			var adminRole = new Role();
			adminRole.setName("ADMIN");
			adminRole.setAssignedUsers(List.of("114249895833464720724"));
			roleService.createRole(adminRole);
		};
	}

	private Form createSecondForm(FormService formService) {
		Form form = new Form(null, UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"), UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"),
				"My Second Form", "my-second-form", null, false, false);
		Form form1 = formService.createForm(form);
		AssignedFormModule module = new AssignedFormModule(null, "addToVo", form1, null, Map.of("VO", "2"), 0);
		formService.setAssignedModules(form1.getId(), List.of(module));
		formService.setApprovalGroups(form1.getId(), List.of(new ApprovalGroup(null, form1, 0, false, 1, UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"))));

		FormItem formItem = new FormItem(null, form1, "login", 0, false, false, FormItem.Type.USERNAME,
				false, null, null, EINFRA_LOGIN, EINFRA_LOGIN,
				List.of(new ItemTexts(null, Locale.ENGLISH, "einfra", null, null, null)),
				null,
				List.of(Form.FormType.INITIAL, Form.FormType.EXTENSION),
				null, null, FormItem.Disabled.NEVER, FormItem.Hidden.NEVER, false);
		formService.setFormItems(2L, List.of(formItem));

		return form1;
	}

	private Form createFirstForm(FormService formService) {
		Form form = new Form(null, UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"), UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"),
				"My First Form", "my-first-form", null, false, false);
		Form form1 = formService.createForm(form);
		AssignedFormModule module = new AssignedFormModule(null, "addToVo", form1, null, Map.of("VO", "3"), 0);
		formService.setAssignedModules(form1.getId(), List.of(module));
		formService.setApprovalGroups(form1.getId(), List.of(new ApprovalGroup(null, form1, 0, false, 1, UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"))));

		FormItem formItem = new FormItem(null, form1, "login", 0, true, false, FormItem.Type.USERNAME,
				false, null, null, EINFRA_LOGIN, EINFRA_LOGIN,
				List.of(new ItemTexts(null, Locale.ENGLISH, "einfra", null, null, null)),
				null,
				List.of(Form.FormType.INITIAL, Form.FormType.EXTENSION),
				null, null, FormItem.Disabled.NEVER, FormItem.Hidden.NEVER, false);
		formService.setFormItems(1L, List.of(formItem));

		return form1;
	}
}
