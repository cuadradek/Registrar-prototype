package cz.metacentrum.registrar.service.idm.perun;

import cz.metacentrum.perun.openapi.PerunRPC;
import cz.metacentrum.perun.openapi.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class PerunEnhancedRPC extends PerunRPC {

	@Value("${perun.primary-ext-source}")
	private String primaryExtSource;

	public PerunEnhancedRPC(String perunURL, String username, String password, RestTemplate restTemplate) {
		super(perunURL, username, password, restTemplate);
	}

	public User getUserByIdentifier(String userIdentifier) {
		try {
			return super.getUsersManager().getUserByExtSourceNameAndExtLogin(userIdentifier, primaryExtSource);
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
				return null;
			} else {
				throw ex;
			}
		}
	}
}
