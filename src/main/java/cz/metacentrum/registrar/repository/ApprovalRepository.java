package cz.metacentrum.registrar.repository;

import cz.metacentrum.registrar.model.Approval;
import cz.metacentrum.registrar.model.SubmittedForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
	List<Approval> findApprovalByLevelAndSubmittedFormAndDecision(int level, SubmittedForm submittedForm, Approval.Decision decision);
}
