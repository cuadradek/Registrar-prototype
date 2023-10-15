package cz.metacentrum.registrar.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PrincipalService {

	public RegistrarPrincipal getPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			return new RegistrarPrincipal("TEST_NAME", Map.of("sub", "TEST_ID@CESNET.CZ"), List.of());
		}

		return (RegistrarPrincipal) authentication.getPrincipal();
	}
}
