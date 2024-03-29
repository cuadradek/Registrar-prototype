package cz.metacentrum.registrar.security;


import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class RegistrarUnauthenticatedPrincipal extends RegistrarPrincipal {

	@Override
	public Map<String, Object> getAttributes() {
		return Collections.emptyMap();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		return false;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public boolean isMfa() {
		return false;
	}

	@Override
	public Map<String, Object> getClaims() {
		return Collections.emptyMap();
	}
}
