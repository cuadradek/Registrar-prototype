package cz.metacentrum.registrar.security;

import lombok.Data;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimAccessor;

import java.util.Set;
import java.util.UUID;

@Data
public abstract class RegistrarPrincipal implements OAuth2AuthenticatedPrincipal, OAuth2TokenIntrospectionClaimAccessor {

	private Set<UUID> idmGroups;
	private Set<Long> formManager;
	private Set<Long> formApprover;

	public abstract boolean isAuthenticated();
	public abstract String getName();
	public abstract String getId();
	public abstract boolean isMfa();
}
