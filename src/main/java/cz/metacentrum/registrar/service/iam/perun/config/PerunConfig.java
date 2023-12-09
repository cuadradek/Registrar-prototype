package cz.metacentrum.registrar.service.iam.perun.config;

import cz.metacentrum.perun.openapi.UsersManagerApi;
import cz.metacentrum.perun.openapi.invoker.ApiClient;
import cz.metacentrum.registrar.service.iam.perun.client.PerunEnhancedRPC;
import cz.metacentrum.registrar.service.iam.perun.client.PerunRPCResponseErrorHandler;
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

	@Bean
	public PerunEnhancedRPC PerunRPC() {
		var restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new PerunRPCResponseErrorHandler());
		return new PerunEnhancedRPC(perunUrl, perunUser, perunPassword, restTemplate);
	}
}
