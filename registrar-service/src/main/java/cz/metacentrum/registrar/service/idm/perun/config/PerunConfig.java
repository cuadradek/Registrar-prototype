package cz.metacentrum.registrar.service.idm.perun.config;

import cz.metacentrum.perun.openapi.PerunRPC;
import cz.metacentrum.perun.openapi.UsersManagerApi;
import cz.metacentrum.perun.openapi.invoker.ApiClient;
import cz.metacentrum.registrar.service.idm.perun.PerunEnhancedRPC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PerunConfig {

	@Value("${perun.rpc-url}")
	private String perunUrl;
	@Value("${perun.user}")
	private String perunUser;
	@Value("${perun.password}")
	private String perunPassword;

	@Bean
	public ApiClient apiClient() {
		var apiClient = new ApiClient();
		apiClient.setBasePath(perunUrl);
		apiClient.setUsername(perunUser);
		apiClient.setPassword(perunPassword);
		return apiClient;
	}

	@Bean
	public UsersManagerApi usersManagerApi() {
		return new UsersManagerApi(apiClient());
	}

//	@Bean
//	public PerunRPC PerunRPC() {
//		return new PerunRPC(perunUrl, perunUser, perunPassword, new RestTemplate());
//	}
	@Bean
	public PerunEnhancedRPC PerunRPC() {
		return new PerunEnhancedRPC(perunUrl, perunUser, perunPassword, new RestTemplate());
	}
}
