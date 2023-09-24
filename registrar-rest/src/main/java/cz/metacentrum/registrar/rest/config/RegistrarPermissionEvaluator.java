package cz.metacentrum.registrar.rest.config;

import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.service.IdmApi;
import cz.metacentrum.registrar.service.RoleService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashSet;

/**
 * 2 options how to use RegistrarPermissionEvaluator:
 * 		1. as a bean and then just:
 * 		- @PreAuthorize("@registrarPermissionEvaluator.hasRole(#id, 'FORM_MANAGER')")
 * 		2. class implementing PermissionEvaluator
 * 		- needs to be registered in MethodSecurityExpressionHandler bean
 * 		- @PreAuthorize("hasPermission(#id, 'FORM_MANAGER')")
 */
@Component
public class RegistrarPermissionEvaluator implements PermissionEvaluator {

	private final IdmApi idmApi;
	private final FormService formService;
	private final RoleService roleService;

	public RegistrarPermissionEvaluator(IdmApi idmApi, FormService formService, RoleService roleService) {
		this.idmApi = idmApi;
		this.formService = formService;
		this.roleService = roleService;
	}

	public boolean hasRole(Long objectId, String permission) {
		if ((objectId == null) || permission == null) {
			return false;
		}

		RegistrarPrincipal principal = initRegistrarPrincipal(SecurityContextHolder.getContext().getAuthentication());
		if ("FORM_MANAGER".equals(permission)) {
			return principal.getFormManager().contains(objectId);
		} else if ("FORM_APPROVER".equals(permission)) {
			return principal.getFormApprover().contains(objectId);
		}

		return false;
//		return hasPrivilege(auth, targetType.toUpperCase(), permission.toString().toUpperCase());
	}

	@Override
	public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
		if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
			return false;
		}

		Long id = (Long) targetId;
		RegistrarPrincipal principal = initRegistrarPrincipal(auth);
		if ("FORM_MANAGER".equals(permission)) {
			return principal.getFormManager().contains(id);
		} else if ("FORM_APPROVER".equals(permission)) {
			return principal.getFormApprover().contains(id);
		}

		return false;
//		return hasPrivilege(auth, targetType.toUpperCase(), permission.toString().toUpperCase());
	}

	@Override
	public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
		if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)){
			return false;
		}
		String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();

		Long id = (Long) targetDomainObject;
		RegistrarPrincipal principal = initRegistrarPrincipal(auth);
		if ("FORM_MANAGER".equals(permission)) {
			return principal.getFormManager().contains(id);
		} else if ("FORM_APPROVER".equals(permission)) {
			return principal.getFormApprover().contains(id);
		}

		return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
	}

	private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
		RegistrarPrincipal principal = initRegistrarPrincipal(auth);

		return false;
	}

	private RegistrarPrincipal initRegistrarPrincipal(Authentication auth) {
		RegistrarPrincipal principal = (RegistrarPrincipal) auth.getPrincipal();
		if (principal.getIdmGroups() != null) {
			return principal;
		}

		principal.setIdmGroups(new HashSet<>(idmApi.getUserGroups(principal.getName())));
		principal.setFormApprover(new HashSet<>(formService.getFormsByIdmApprovalGroups(principal.getIdmGroups())));
		principal.setFormManager(new HashSet<>(formService.getFormsByIdmManagersGroups(principal.getIdmGroups())));
		return principal;
	}

}