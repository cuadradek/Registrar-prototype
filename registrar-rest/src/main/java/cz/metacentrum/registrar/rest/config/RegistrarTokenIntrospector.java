package cz.metacentrum.registrar.rest.config;

import cz.metacentrum.registrar.service.RegistrarPrincipal;
import cz.metacentrum.registrar.service.RoleService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * We need custom TokenIntrospector for 2 reasons:
 * - make introspect method cacheable
 * - extend OAuth2AuthenticatedPrincipal returned from introspect method,
 *  but maybe this can be done in custom AuthenticationProvider instead
 */
public class RegistrarTokenIntrospector extends SpringOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

	private final RoleService roleService;

	public RegistrarTokenIntrospector(String introspectionUri, String clientId, String clientSecret, RoleService roleService) {
		super(introspectionUri, clientId, clientSecret);
		this.roleService = roleService;
	}

	@Cacheable("token_introspection")
	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		OAuth2AuthenticatedPrincipal principal = super.introspect(token);

		List<String> roles = roleService.getRolesByUserIdentifier(principal.getName());
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		for (String role : roles) {
			// authority ROLE_x can be used as hasRole(x) or hasAuthority(ROLE_x)
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
		}
		authorities.addAll(principal.getAuthorities());

		return new RegistrarPrincipal(principal.getAttributes(), authorities);
	}
}
