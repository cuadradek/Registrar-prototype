package cz.metacentrum.registrar.service.idm.perun;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class PerunHttp {
	public static final String MEMBERS_MANAGER = "membersManager";
	public static final String USERS_MANAGER = "usersManager";
	public static final String CESNET_EXT_SOURCE = "https://login.cesnet.cz/idp/";
	public static final String INTERNAL_EXT_SOURCE = "INTERNAL";

	@Value("${perun.rpc-url}")
	private String perunUrl;
	@Value("${perun.user}")
	private String perunUser;
	@Value("${perun.password}")
	private String perunPassword;
	private WebClient client;

	@PostConstruct
	private void initWebClient() {
		this.client = WebClient.builder()
				.baseUrl(perunUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//				.defaultHeader(HttpHeaders.AUTHORIZATION, HttpHeaders.encodeBasicAuth(perunUser, perunPassword, null))
				.defaultHeaders(headers -> headers.setBasicAuth(perunUser, perunPassword))
				.defaultUriVariables(Collections.singletonMap("url", perunUrl))
				.build();
	}

	public Member createMember(int userId, int voId) {
		String actionUrl = "/json/" + MEMBERS_MANAGER + '/' + "createMember";
		//vo, user
		Map<String, String> bodyMap = new HashMap();
		bodyMap.put("vo", String.valueOf(voId));
		bodyMap.put("user", String.valueOf(userId));
		Mono<Member> response = client.post()
				.uri(uriBuilder -> uriBuilder
						.path(actionUrl)
						.queryParam("vo", voId)
						.queryParam("user", userId)
						.build())
				.body(BodyInserters.fromValue(bodyMap))
				.retrieve()
				.bodyToMono(Member.class);
		return response.block();
	}

	public Member getMemberByUserAndVo(int userId, int voId) {
		String actionUrl = "/json/" + MEMBERS_MANAGER + '/' + "getMemberByUser";
		try {
			Mono<Member> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("vo", voId)
							.queryParam("user", userId)
							.build())
					.retrieve()
					.bodyToMono(Member.class);
			return response.block();
		} catch (WebClientRequestException ex) {
			return null;
		} catch (WebClientResponseException ex) {
			return null;
		}
	}

	public List<UUID> getUserGroups() {
		//todo make actual request
		return List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"),
				UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"));
	}

	public User getUserByIdentifier(String userIdentifier) {
		String actionUrl = "/json/" + USERS_MANAGER + '/' + "getUserByExtSourceNameAndExtLogin";
		try {
			Mono<User> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("extLogin", userIdentifier)
//							.queryParam("extSourceName", INTERNAL_EXT_SOURCE)
							.queryParam("extSourceName", CESNET_EXT_SOURCE)
							.build())
					.retrieve()
					.bodyToMono(User.class);
			return response.block();
		} catch (WebClientRequestException ex) {
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

}
