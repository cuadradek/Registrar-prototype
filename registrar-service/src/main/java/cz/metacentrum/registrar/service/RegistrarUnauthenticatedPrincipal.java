package cz.metacentrum.registrar.service;


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
//		return "perunaaa"; //only for local tests
		return "perun"; //only for local tests
//		return null;
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
