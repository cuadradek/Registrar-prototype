package cz.metacentrum.registrar.rest.config;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;

/**
 * We need custom TokenIntrospector for 2 reasons:
 * - make introspect method cacheable
 * - extend OAuth2AuthenticatedPrincipal returned from introspect method,
 *  but maybe this can be done in custom AuthenticationProvider instead
 */
public class RegistrarTokenIntrospector extends SpringOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

	public RegistrarTokenIntrospector(String introspectionUri, String clientId, String clientSecret) {
		super(introspectionUri, clientId, clientSecret);
	}

	@Cacheable("token_introspection")
	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		OAuth2AuthenticatedPrincipal principal = super.introspect(token);
		return new RegistrarPrincipal(principal);
	}
}
