package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Role;
import cz.metacentrum.registrar.persistence.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
	private final RoleRepository roleRepository;

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public List<String> getRolesByUserIdentifier(String userIdentifier) {
		return roleRepository.getRolesByUserIdentifier(userIdentifier);
	}

	@Override
	public void createRole(Role role) {
		roleRepository.save(role);
	}
}
