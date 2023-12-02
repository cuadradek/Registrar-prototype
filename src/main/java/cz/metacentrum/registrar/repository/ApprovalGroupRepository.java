package cz.metacentrum.registrar.repository;

import cz.metacentrum.registrar.model.ApprovalGroup;
import cz.metacentrum.registrar.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalGroupRepository extends JpaRepository<ApprovalGroup, Long> {
	List<ApprovalGroup> getAllByForm(Form form);

}
