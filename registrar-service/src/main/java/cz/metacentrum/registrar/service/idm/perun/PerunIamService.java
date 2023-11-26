package cz.metacentrum.registrar.service.idm.perun;

import cz.metacentrum.registrar.service.IamService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PerunIamService implements IamService {

	private final PerunHttp perunHttp;

	public PerunIamService(PerunHttp perunHttp) {
		this.perunHttp = perunHttp;
	}

	@Override
	public List<UUID> getUserGroups(String userIdentifier) {
		// todo implement this
		return perunHttp.getUserGroups();
	}

	@Override
	public boolean canCreateForm(String userIdentifier) {
		return false;
	}

	@Override
	public boolean isObjectRightHolder(String userIdentifier, UUID iamObject) {
		return false;
	}

	@Override
	public List<String> getUserRoles(String userIdentifier) {
		return null;
	}
}
