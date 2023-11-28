package cz.metacentrum.registrar.rest.config;

import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.service.IamService;
import cz.metacentrum.registrar.service.PrincipalService;
import cz.metacentrum.registrar.service.RegistrarPrincipal;
import cz.metacentrum.registrar.service.RoleService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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

	private final IamService iamService;
	private final FormService formService;
	private final RoleService roleService;
	private final PrincipalService principalService;

	public RegistrarPermissionEvaluator(IamService iamService, FormService formService, RoleService roleService, PrincipalService principalService) {
		this.iamService = iamService;
		this.formService = formService;
		this.roleService = roleService;
		this.principalService = principalService;
	}

	public boolean hasRole(Long objectId, String permission) {
		if ((objectId == null) || permission == null) {
			return false;
		}

		RegistrarPrincipal principal = initRegistrarPrincipal();
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
		RegistrarPrincipal principal = initRegistrarPrincipal();
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
		RegistrarPrincipal principal = initRegistrarPrincipal();
		if ("FORM_MANAGER".equals(permission)) {
			return principal.getFormManager().contains(id);
		} else if ("FORM_APPROVER".equals(permission)) {
			return principal.getFormApprover().contains(id);
		}

		return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
	}

	private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
		RegistrarPrincipal principal = initRegistrarPrincipal();

		return false;
	}

	private RegistrarPrincipal initRegistrarPrincipal() {
		RegistrarPrincipal principal = principalService.getPrincipal();
		if (principal.getIdmGroups() != null) {
			return principal;
		}

		if (principal.isAuthenticated()) {
			principal.setIdmGroups(new HashSet<>(iamService.getUserGroups(principal.getId())));
			principal.setFormApprover(new HashSet<>(formService.getFormsByIdmApprovalGroups(principal.getIdmGroups())));
			principal.setFormManager(new HashSet<>(formService.getFormsByIdmManagersGroups(principal.getIdmGroups())));
		} else {
			principal.setIdmGroups(Set.of());
			principal.setFormApprover(Set.of());
			principal.setFormManager(Set.of());
		}
		return principal;
	}

}
