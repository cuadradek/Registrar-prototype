package cz.metacentrum.registrar.rest.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.metacentrum.registrar.service.RegistrarOAuth2Principal;
import cz.metacentrum.registrar.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * We need custom TokenIntrospector for 2 reasons:
 * - make introspect method cacheable
 * - extend OAuth2AuthenticatedPrincipal returned from introspect method,
 *  but maybe this can be done in custom AuthenticationProvider instead
 */
@Slf4j
public class RegistrarTokenIntrospector extends SpringOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

	private final RoleService roleService;
	private String userInfoEndpoint;
	private final WebClient webClient = WebClient.create();
	private final ObjectMapper objectMapper = new ObjectMapper();

	public RegistrarTokenIntrospector(String introspectionUri, String clientId, String clientSecret, String userInfoEndpoint, RoleService roleService) {
		super(introspectionUri, clientId, clientSecret);
		this.userInfoEndpoint = userInfoEndpoint;
		this.roleService = roleService;
	}

	@Cacheable("token_introspection")
	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		OAuth2AuthenticatedPrincipal principal = super.introspect(token);
		makeUserInfoRequest(token);

		List<String> roles = roleService.getRolesByUserIdentifier(principal.getName());
		Collection<GrantedAuthority> authorities = roles.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // authority ROLE_x can be used as hasRole(x) or hasAuthority(ROLE_x)
				.collect(Collectors.toList());
		authorities.addAll(principal.getAuthorities());

		Map<String, Object> claims = makeUserInfoRequest(token);
		claims.putAll(principal.getAttributes());

		return new RegistrarOAuth2Principal(claims, authorities);
	}

	private Map<String, Object> makeUserInfoRequest(String token) {
		String response = webClient.get()
				.uri(userInfoEndpoint)
				.header("Authorization", "Bearer " + token)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		try {
			return objectMapper.readValue(response, Map.class);
		} catch (JsonProcessingException e) {
			log.error("Error while parsing user info response.", e);
			return new HashMap<>();
		}
	}
}
