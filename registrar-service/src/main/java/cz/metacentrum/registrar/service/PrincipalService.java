package cz.metacentrum.registrar.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PrincipalService {

	public RegistrarPrincipal getPrincipal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (RegistrarPrincipal) authentication.getPrincipal();
	}
}
