package cz.metacentrum.registrar.service.idm.perun;

import cz.metacentrum.perun.openapi.PerunRPC;
import cz.metacentrum.perun.openapi.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class PerunEnhancedRPC extends PerunRPC {

	@Value("${perun.primary-ext-source}")
	private String primaryExtSource;

	public PerunEnhancedRPC(String perunURL, String username, String password, RestTemplate restTemplate) {
		super(perunURL, username, password, restTemplate);
	}

	public Optional<User> getUserByIdentifier(String userIdentifier) {
		try {
			return Optional.of(super.getUsersManager().getUserByExtSourceNameAndExtLogin(userIdentifier, primaryExtSource));
		} catch (PerunRuntimeException ex) {
			if (ex.getName().equals("UserExtSourceNotExistsException")) {
				return Optional.empty();
			} else {
				throw ex;
			}
		}
	}
}
