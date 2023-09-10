package cz.metacentrum.registrar.rest.config;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Check OAuth2IntrospectionAuthenticatedPrincipal and DefaultOAuth2AuthenticatedPrincipal
 */
@Data
public class RegistrarPrincipal implements OAuth2AuthenticatedPrincipal {
	// DefaultOAuth2AuthenticatedPrincipal by default
	// or we can pass it in constructor or OAuth2IntrospectionAuthenticatedPrincipal if we need more methods
	private final OAuth2AuthenticatedPrincipal delegate;
	private Set<UUID> idmGroups;
	private Set<Long> formManager;
	private Set<Long> formApprover;

	public RegistrarPrincipal(Map<String, Object> attributes, Collection<GrantedAuthority> authorities) {
		this.delegate = new DefaultOAuth2AuthenticatedPrincipal(attributes, authorities);
	}

	public RegistrarPrincipal(String name, Map<String, Object> attributes, Collection<GrantedAuthority> authorities) {
		this.delegate = new DefaultOAuth2AuthenticatedPrincipal(name, attributes, authorities);
	}

	public RegistrarPrincipal(OAuth2AuthenticatedPrincipal delegate) {
		this.delegate = delegate;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return delegate.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return delegate.getAuthorities();
	}

	@Override
	public String getName() {
		return delegate.getName();
	}
}
