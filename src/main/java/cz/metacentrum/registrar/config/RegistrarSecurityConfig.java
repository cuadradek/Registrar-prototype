package cz.metacentrum.registrar.config;

import cz.metacentrum.registrar.security.RegistrarTokenIntrospector;
import cz.metacentrum.registrar.security.RegistrarUnauthenticatedFilter;
import cz.metacentrum.registrar.service.RoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class RegistrarSecurityConfig {

	@Value("${registrar.oauth2.resourceserver.introspection-uri}")
	private String introspectionUri;

	@Value("${registrar.oauth2.resourceserver.client-id}")
	private String clientId;

	@Value("${registrar.oauth2.resourceserver.client-secret}")
	private String clientSecret;

	@Value("${registrar.oauth2.resourceserver.userinfo-uri}")
	private String userInfoEndpoint;

	@Bean
	@Profile("!local")
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				authorize -> authorize
						.requestMatchers("/submissions").permitAll()
						.requestMatchers("/submissions/load").permitAll()
						.requestMatchers("/swagger-ui/**").permitAll()
						.requestMatchers("/v3/api-docs/**").permitAll()
						// if the request didn't match any previous matcher,
						// then user needs to be authenticated with OIDC and his token to have scope REGISTRAR_API
						.anyRequest().hasAuthority("SCOPE_REGISTRAR_API")
		)
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)
				.addFilterAfter(new RegistrarUnauthenticatedFilter(), AnonymousAuthenticationFilter.class)
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}

	@Bean
	@Profile("local")
	public SecurityFilterChain filterChainLocalProfile(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(
						authorize -> authorize
								.requestMatchers("/swagger-ui/**").permitAll()
								.requestMatchers("/v3/api-docs/**").permitAll()
								.requestMatchers("/submissions").permitAll()
								.requestMatchers("/submissions/load").permitAll()
								.requestMatchers("/submissions/**").permitAll()
								.requestMatchers("/submitted-forms/**").permitAll()
								.requestMatchers("/forms/**").permitAll()
								.requestMatchers("/test/**").hasAuthority("SCOPE_openid")
				)
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)
				.addFilterAfter(new RegistrarUnauthenticatedFilter(), AnonymousAuthenticationFilter.class)
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}

	@Bean
	public OpaqueTokenIntrospector introspector(RoleService roleService) {
		return new RegistrarTokenIntrospector(introspectionUri, clientId, clientSecret, userInfoEndpoint, roleService);
	}
}
