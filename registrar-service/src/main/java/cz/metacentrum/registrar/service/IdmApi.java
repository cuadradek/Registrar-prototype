package cz.metacentrum.registrar.service;

import java.util.List;
import java.util.UUID;

public interface IdmApi {

	List<UUID> getUserGroups(String userIdentifier);
}
