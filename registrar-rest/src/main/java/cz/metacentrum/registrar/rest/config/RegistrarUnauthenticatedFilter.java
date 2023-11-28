package cz.metacentrum.registrar.rest.config;

import cz.metacentrum.registrar.service.RegistrarUnauthenticatedPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

public class RegistrarUnauthenticatedFilter extends GenericFilterBean {


	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			Authentication a = new AnonymousAuthenticationToken("unauthenticated",
					new RegistrarUnauthenticatedPrincipal(), List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
			SecurityContextHolder.getContext().setAuthentication(a);
		}

		filterChain.doFilter(servletRequest, servletResponse);

	}
}
