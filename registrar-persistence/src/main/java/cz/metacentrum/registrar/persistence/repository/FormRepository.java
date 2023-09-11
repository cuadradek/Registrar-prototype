package cz.metacentrum.registrar.persistence.repository;

import cz.metacentrum.registrar.persistence.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
	List<Form> getAllByIdIn(Set<Long> ids);
	@Query("SELECT f.id FROM Form f WHERE f.idmFormManagersGroup IN ?1")
	List<Long> findIdsByIdmFormManagersGroup(Set<UUID> groupUUIDs);
	@Query("SELECT f.id FROM Form f JOIN f.approvalGroups a WHERE a.idmGroup IN ?1")
//	@Query("SELECT f.id FROM Form f JOIN ApprovalGroup a WHERE a.idmGroup IN ?1")
	List<Long> findIsByIdmApprovalGroups(Set<UUID> groupUUIDs);
}
