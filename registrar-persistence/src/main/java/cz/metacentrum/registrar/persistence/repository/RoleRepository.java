package cz.metacentrum.registrar.persistence.repository;


import cz.metacentrum.registrar.persistence.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	@Query(value = "SELECT r.name FROM role r JOIN roles_users u ON r.id = u.role_id WHERE u.user_id = ?1", nativeQuery = true)
	List<String> getRolesByUserIdentifier(String userIdentifier);
}
