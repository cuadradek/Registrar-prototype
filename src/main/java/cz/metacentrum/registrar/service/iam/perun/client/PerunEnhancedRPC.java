package cz.metacentrum.registrar.service.iam.perun.client;

import cz.metacentrum.perun.openapi.PerunRPC;
import cz.metacentrum.perun.openapi.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PerunEnhancedRPC extends PerunRPC {

	public static final String USERS_MANAGER = "usersManager";

	@Value("${perun.primary-ext-source}")
	private String primaryExtSource;

	private final WebClient client;

	public PerunEnhancedRPC(String perunURL, String username, String password, RestTemplate restTemplate) {
		super(perunURL, username, password, restTemplate);
		this.client = WebClient.builder()
				.baseUrl(perunURL)
				.baseUrl(perunURL)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeaders(headers -> headers.setBasicAuth(username, password))
				.defaultUriVariables(Collections.singletonMap("url", perunURL))
				.build();
	}

	/**
	 * This method is implemented here because its corresponding method from the perun-openapi library
	 * is incorrectly escaped when passing URL as ext source name.
	 * @param userIdentifier
	 * @return
	 */
	public Optional<User> getUserByIdentifier(@Nullable String userIdentifier) {
		if (userIdentifier == null) {
			return Optional.empty();
		}

		String actionUrl = "/json/" + USERS_MANAGER + '/' + "getUserByExtSourceNameAndExtLogin";
		try {
			Mono<Map<String, String>> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("extLogin", userIdentifier)
							.queryParam("extSourceName", primaryExtSource)
							.build())
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<>() {});

			return Optional.of(mapUser(response.block()));
		} catch (WebClientResponseException.BadRequest ex) {
			return Optional.empty();
		}
	}

	private User mapUser(Map<String, String> userMap) {
		User user = new User();
		user.setId(Integer.valueOf(userMap.get("id")));
		user.setFirstName(userMap.get("firstName"));
		user.setLastName(userMap.get("lastName"));
		user.setMiddleName(userMap.get("middleName"));
		user.setTitleAfter(userMap.get("titleAfter"));
		user.setTitleBefore(userMap.get("titleBefore"));
		user.setServiceUser(Boolean.valueOf(userMap.get("serviceUser")));
		user.setSpecificUser(Boolean.valueOf(userMap.get("specificUser")));
		user.setSponsoredUser(Boolean.valueOf(userMap.get("sponsoredUser")));
		user.setUuid(UUID.fromString(userMap.get("uuid")));
		user.setUuid(UUID.fromString(userMap.get("uuid")));
		user.setBeanName(userMap.get("beanName"));
		user.setMajorSpecificType(userMap.get("NORMAL"));
		return user;
	}
}
