package cz.metacentrum.registrar.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class RegistrarSecurityConfig {

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}")
	private String introspectionUri;

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.resourceserver.opaquetoken.client-secret}")
	private String clientSecret;


//	@Bean
//	public MethodSecurityExpressionHandler createExpressionHandler() {
//		DefaultMethodSecurityExpressionHandler expressionHandler =
//				new DefaultMethodSecurityExpressionHandler();
//		expressionHandler.setPermissionEvaluator(new RegistrarPermissionEvaluator());
//		return expressionHandler;
//	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(
						authorize -> authorize
								.requestMatchers("/submissions/**").permitAll()
								.requestMatchers("/forms/**").permitAll()
								.requestMatchers("/test/**").hasAuthority("SCOPE_openid")
								//if the request didn't match test/**, then try to match this:
								.requestMatchers("/forms/**").hasAuthority("SCOPE_REGISTRAR_API")
								// if the request didn't match any ant matcher, then user needs to be at least authenticated
								.anyRequest().authenticated()
				)
//				.oauth2ResourceServer(oauth2 -> oauth2.opaqueToken
//						(token -> token.introspectionUri(this.introspectionUri)
//								.introspectionClientCredentials(this.clientId, this.clientSecret)
//								.introspector(introspector()))
//				);
//				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}

	@Bean
	public OpaqueTokenIntrospector introspector() {
		return new GoogleTokenIntrospector(introspectionUri);
//		return new RegistrarTokenIntrospector(introspectionUri, clientId, clientSecret);
//		return new SpringOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
//		return new NimbusOpaqueTokenIntrospector(introspectionUri, clientId, clientSecret);
	}
}
