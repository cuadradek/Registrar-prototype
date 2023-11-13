package cz.metacentrum.registrar.rest.controller;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.service.RegistrarPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

	@GetMapping("/hello")
	@PreAuthorize("hasAuthority('SCOPE_openid')")
	public String hello(@AuthenticationPrincipal RegistrarPrincipal principal) {
		log.info("Som v hello metode");
//		return "hello " + principal.getFormApprover();
		return "hello " + (principal == null ? null : principal.getName());
	}

	@GetMapping("/principal")
	public RegistrarPrincipal getPrincipal(@AuthenticationPrincipal RegistrarPrincipal principal) {
		return principal;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin")
	public String admin(@AuthenticationPrincipal RegistrarPrincipal principal) {
		return "Hello admin " + principal.getName();
	}

	@GetMapping("/forms/{id}")
	@PreAuthorize("@registrarPermissionEvaluator.hasRole(#id, 'FORM_MANAGER')")
//	@PreAuthorize("hasPermission(#id, 'FORM_MANAGER')")
	public Form getForm(@PathVariable Long id) {
		var form = new Form();
		form.setId(id);
		return form;
	}
}
