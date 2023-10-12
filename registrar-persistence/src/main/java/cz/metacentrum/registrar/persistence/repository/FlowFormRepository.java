package cz.metacentrum.registrar.persistence.repository;

import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowFormRepository extends JpaRepository<AssignedFlowForm, Long> {
	List<AssignedFlowForm> getAllByMainForm(Form mainForm);

}
