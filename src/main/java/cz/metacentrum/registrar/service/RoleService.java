package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.model.Role;

import java.util.List;

public interface RoleService {
	List<String> getRolesByUserIdentifier(String userIdentifier);
	void createRole(Role role);
}
