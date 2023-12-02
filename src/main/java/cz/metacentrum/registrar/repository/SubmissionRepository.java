package cz.metacentrum.registrar.repository;

import cz.metacentrum.registrar.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
	List<Submission> getAllBySubmitterId(String submitterId);
}
