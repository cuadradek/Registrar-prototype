package cz.metacentrum.registrar.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.Map;

/**
 * Check OAuth2IntrospectionAuthenticatedPrincipal and DefaultOAuth2AuthenticatedPrincipal
 */
@Data
public class RegistrarOAuth2Principal extends RegistrarPrincipal {
	// DefaultOAuth2AuthenticatedPrincipal by default
	private final OAuth2AuthenticatedPrincipal delegate;

	public RegistrarOAuth2Principal(Map<String, Object> attributes, Collection<GrantedAuthority> authorities) {
		this.delegate = new DefaultOAuth2AuthenticatedPrincipal(attributes, authorities);
	}

	public RegistrarOAuth2Principal(String name, Map<String, Object> attributes, Collection<GrantedAuthority> authorities) {
		this.delegate = new DefaultOAuth2AuthenticatedPrincipal(name, attributes, authorities);
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
		Object name = this.getAttributes().get("name");
		return name == null ? delegate.getName() : (String) name;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public String getId() {
		return (String) this.getAttributes().get("sub");
	}

	@Override
	public boolean isMfa() {
		Object acrClaim = this.getAttributes().get("acr");
		return acrClaim != null && acrClaim.equals("https://refeds.org/profile/mfa");
	}

	@Override
	public Map<String, Object> getClaims() {
		return this.getAttributes();
	}
}
