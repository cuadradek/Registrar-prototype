package cz.metacentrum.registrar.persistence.repository;

import cz.metacentrum.registrar.persistence.entity.Approval;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
	List<Approval> findApprovalByLevelAndSubmittedFormAndDecision(int level, SubmittedForm submittedForm, Approval.Decision decision);
}
