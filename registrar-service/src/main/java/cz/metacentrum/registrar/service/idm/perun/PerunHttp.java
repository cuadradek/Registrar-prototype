package cz.metacentrum.registrar.service.idm.perun;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
import java.util.Set;
import java.util.UUID;

@Component
public class PerunHttp {
	public static final String MEMBERS_MANAGER = "membersManager";
	public static final String USERS_MANAGER = "usersManager";
	public static final String GROUPS_MANAGER = "groupsManager";
	public static final String AUTHZ_RESOLVER = "authzResolver";
	public static final String ATTRIBUTES_MANAGER = "attributesManager";

	@Value("${perun.primary-ext-source}")
	private String primaryExtSource;

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

	public MemberHttp createMember(int userId, int voId) {
		String actionUrl = "/json/" + MEMBERS_MANAGER + '/' + "createMember";
		//vo, user
		Map<String, String> bodyMap = new HashMap();
		bodyMap.put("vo", String.valueOf(voId));
		bodyMap.put("user", String.valueOf(userId));
		Mono<MemberHttp> response = client.post()
				.uri(uriBuilder -> uriBuilder
						.path(actionUrl)
						.queryParam("vo", voId)
						.queryParam("user", userId)
						.build())
				.body(BodyInserters.fromValue(bodyMap))
				.retrieve()
				.bodyToMono(MemberHttp.class);
		return response.block();
	}

	public MemberHttp getMemberByUserAndVo(int userId, int voId) {
		String actionUrl = "/json/" + MEMBERS_MANAGER + '/' + "getMemberByUser";
		try {
			Mono<MemberHttp> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("vo", voId)
							.queryParam("user", userId)
							.build())
					.retrieve()
					.bodyToMono(MemberHttp.class);
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

	public UserHttp getUserByIdentifier(String userIdentifier) {
		String actionUrl = "/json/" + USERS_MANAGER + '/' + "getUserByExtSourceNameAndExtLogin";
		try {
			Mono<UserHttp> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("extLogin", userIdentifier)
							.queryParam("extSourceName", primaryExtSource)
							.build())
					.retrieve()
					.bodyToMono(UserHttp.class);
			return response.block();
		} catch (WebClientRequestException ex) {
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	public Map<String, Map<String, Set<Integer>>> getUserRoles(int userId) {
		String actionUrl = "/json/" + AUTHZ_RESOLVER + '/' + "getUserRoles";
		try {
			Mono<Map<String, Map<String, Set<Integer>>>> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("userId", userId)
							.build())
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<>() {});
			return response.block();
		} catch (WebClientRequestException ex) {
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	public List<VOHttp> getVosWhereUserIsMember(int userId) {
		String actionUrl = "/json/" + USERS_MANAGER + '/' + "getVosWhereUserIsMember";
		try {
			Mono<List<VOHttp>> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("userId", userId)
							.build())
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<>() {});
			return response.block();
		} catch (WebClientRequestException ex) {
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	public List<Group> getMemberGroups(int memberId) {
		String actionUrl = "/json/" + GROUPS_MANAGER + '/' + "getMemberGroups";
		try {
			Mono<List<Group>> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("member", memberId)
							.build())
					.retrieve()
					.bodyToMono(new ParameterizedTypeReference<>() {});
			return response.block();
		} catch (WebClientRequestException ex) {
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	public String getAttribute(int userId, String attributeName) {
		String actionUrl = "/json/" + ATTRIBUTES_MANAGER + '/' + "getAttribute";
		try {
			Mono<String> response = client.get()
					.uri(uriBuilder -> uriBuilder
							.path(actionUrl)
							.queryParam("user", userId)
							.queryParam("attributeName", attributeName)
							.build())
					.retrieve()
					.bodyToMono(String.class);
			return response.block();
		} catch (WebClientRequestException ex) {
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

}
