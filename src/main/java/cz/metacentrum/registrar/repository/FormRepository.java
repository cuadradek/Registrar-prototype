package cz.metacentrum.registrar.repository;

import cz.metacentrum.registrar.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
	Optional<Form> getFormByUrlSuffix(String urlSuffix);
	List<Form> getAllByIdIn(Set<Long> ids);
	@Query("SELECT f.id FROM Form f WHERE f.iamFormManagersGroup IN ?1")
	List<Long> findIdsByIamFormManagersGroup(Set<UUID> groupUUIDs);
//	@Query("SELECT f.id FROM Form f JOIN f.approvalGroups a WHERE a.idmGroup IN ?1")
	@Query("SELECT f.id FROM ApprovalGroup a JOIN a.form f WHERE a.iamGroup IN ?1")
//	@Query("SELECT f.id FROM Form f JOIN ApprovalGroup a WHERE a.idmGroup IN ?1")
	List<Long> findIsByIamApprovalGroups(Set<UUID> groupUUIDs);

	List<Form> getFormsByIamObject(UUID idmObject);
}
