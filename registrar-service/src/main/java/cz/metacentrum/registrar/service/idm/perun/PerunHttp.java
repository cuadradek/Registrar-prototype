package cz.metacentrum.registrar.service.idm.perun;

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

	private String perunUrl;
	private String perunUser;
	private String perunPassword;
	private WebClient client;

	private static PerunHttp instance = null;

	private PerunHttp() {
//		TODO better way to handle these properties
		this.perunUrl = "http://localhost:8081/ba/rpc/";
		this.perunUser = "perun";
		this.perunPassword = "test";
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
		}
	}

	public List<UUID> getUserGroups() {
		//todo make actual request
		return List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"),
				UUID.fromString("13d64d76-2ca3-4cf8-b1f4-0befdbef69fc"));
	}

	public User getUserByIdentificator(String userIdentificator) {
		String actionUrl = "/json/" + USERS_MANAGER + '/' + "getMemberByUser";
		try {
			Mono<User> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("extLogin", userIdentificator)
							.queryParam("extSourceName", CESNET_EXT_SOURCE)
							.build())
					.retrieve()
					.bodyToMono(User.class);
			return response.block();
		} catch (WebClientRequestException ex) {
			return null;
		}
	}

}
