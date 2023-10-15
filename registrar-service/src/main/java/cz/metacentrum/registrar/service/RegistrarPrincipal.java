package cz.metacentrum.registrar.service;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimAccessor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Check OAuth2IntrospectionAuthenticatedPrincipal and DefaultOAuth2AuthenticatedPrincipal
 */
@Data
public class RegistrarPrincipal implements OAuth2AuthenticatedPrincipal, OAuth2TokenIntrospectionClaimAccessor {
	// DefaultOAuth2AuthenticatedPrincipal by default
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

	public String getId() {
		return (String) this.getAttributes().get("sub");
	}

	public boolean isMfa() {
		Object mfa = this.getAttributes().get("mfa");
		return mfa != null && Boolean.parseBoolean((String) mfa);
	}

	public Map<String, Object> getClaims() {
		return this.getAttributes();
	}
}
