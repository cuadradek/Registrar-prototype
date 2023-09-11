package cz.metacentrum.registrar.service.idm.perun;

import cz.metacentrum.registrar.service.IdmApi;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PerunIdmApi implements IdmApi {

	private final PerunHttp perunHttp;

	public PerunIdmApi(PerunHttp perunHttp) {
		this.perunHttp = perunHttp;
	}

	@Override
	public List<UUID> getUserGroups(String userIdentifier) {
		// todo implement this
		return perunHttp.getUserGroups();
	}
}
