package cz.metacentrum.registrar.rest.config;

import cz.metacentrum.registrar.service.RoleService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * New introspector for Google OAuth2 is needed, because its introspection
 * endpoint doesn't follow standards:
 * 	- doesn't return isActive claim
 * 	- takes 'access_token' as input param instead of 'token'
 */
public class GoogleTokenIntrospector implements OpaqueTokenIntrospector {
	private final RestTemplate restTemplate = new RestTemplate();
	private final String introspectionUri;

	private final RoleService roleService;

	public GoogleTokenIntrospector(String introspectionUri, RoleService roleService) {
		this.introspectionUri = introspectionUri;
		this.roleService = roleService;
	}

	@Override
	@Cacheable("token_introspection")
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		RequestEntity<?> requestEntity = buildRequest(token);
		OAuth2AuthenticatedPrincipal principal;
		try {
			ResponseEntity<Map<String, Object>> responseEntity = this.restTemplate.exchange(requestEntity, new ParameterizedTypeReference<>() {});
			Map<String, Object> principalMap = responseEntity.getBody();

			principalMap.computeIfPresent("exp", (k, v) -> Instant.EPOCH.plusSeconds(Long.parseLong((String) v)));
			principalMap.computeIfPresent("iat", (k, v) -> Instant.EPOCH.plusSeconds(Long.parseLong((String) v)));
			Collection<GrantedAuthority> authorities = getGrantedAuthorities(principalMap);

//			principal = new OAuth2IntrospectionAuthenticatedPrincipal(principalMap, authorities);
//			principal = new DefaultOAuth2AuthenticatedPrincipal(principalMap, authorities);
			principal = new RegistrarPrincipal(principalMap, authorities);
		} catch (Exception ex) {
			throw new BadOpaqueTokenException(ex.getMessage(), ex);
		}

		return principal;
	}

	private RequestEntity<?> buildRequest(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("access_token", token);

		return new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(introspectionUri));
	}

	private Collection<GrantedAuthority> getGrantedAuthorities(Map<String, Object> principalMap) {
		Collection<GrantedAuthority> authorities = new ArrayList();
		principalMap.computeIfPresent("scope", (k, v) -> {
			if (!(v instanceof String)) {
				return v;
			} else {
				Collection<String> scopes = Arrays.asList(((String)v).split(" "));
				Iterator var4 = scopes.iterator();

				while(var4.hasNext()) {
					String scope = (String)var4.next();
					authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
					// this is just to have some roles (each scope would be user's role)
					authorities.add(new SimpleGrantedAuthority("ROLE_" + scope));
				}

				return scopes;
			}
		});
		List<String> roles = roleService.getRolesByUserIdentifier((String) principalMap.get("sub"));
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
		}
		return authorities;
	}
}
