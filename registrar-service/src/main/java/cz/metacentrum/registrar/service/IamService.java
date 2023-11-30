package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Identity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IamService {

	List<UUID> getUserGroups(String userIdentifier);
	boolean canCreateForm(String userIdentifier);
	boolean isObjectRightHolder(String userIdentifier, UUID iamObject);
	List<String> getUserRoles(String userIdentifier);
	String getUserAttributeValue(String userIdentifier, String attributeName);
	boolean userExists(String userIdentifier);
	List<Identity> getSimilarUsers(Map<String, Object> claims);
}
