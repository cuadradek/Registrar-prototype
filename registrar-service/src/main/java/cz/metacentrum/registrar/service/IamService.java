package cz.metacentrum.registrar.service;

import java.util.List;
import java.util.UUID;

public interface IamService {

	List<UUID> getUserGroups(String userIdentifier);
	boolean canCreateForm(String userIdentifier);
	boolean isObjectRightHolder(String userIdentifier, UUID iamObject);
	List<String> getUserRoles(String userIdentifier);
	String getUserAttributeValue(String userIdentifier, String attributeName);
}
