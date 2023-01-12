package cz.metacentrum.registrar.service.idm.perun;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PerunHttp {
	public static final String MEMBERS_MANAGER = "membersManager";

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

	public static PerunHttp getInstance() {
		if (instance == null) {
			instance = new PerunHttp();
		}
		return instance;
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
}