package cz.metacentrum.registrar.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Profile("local")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class LocalSecurityConfig {

	@Bean
	public RoleHierarchy roleHierarchy() {
		var hierarchy = new RoleHierarchyImpl();
		hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER\n" +
				"ROLE_USER > ROLE_GUEST");
		return hierarchy;
	}

	@Bean
	public MethodSecurityExpressionHandler createExpressionHandler(RoleHierarchy roleHierarchy) {
		var expressionHandler = new DefaultMethodSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy);
//		expressionHandler.setPermissionEvaluator(new RegistrarPermissionEvaluator());
		return expressionHandler;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(
						authorize -> authorize
								.requestMatchers("/swagger-ui/**").permitAll()
								.requestMatchers("/v3/api-docs/**").permitAll()
								.requestMatchers("/submissions/**").permitAll()
								.requestMatchers("/submitted-forms/**").permitAll()
								.requestMatchers("/forms/**").permitAll()
								.requestMatchers("/test/**").hasAuthority("SCOPE_openid")
				)
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}
}
