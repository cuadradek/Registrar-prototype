package cz.metacentrum.registrar.repository;

import cz.metacentrum.registrar.model.AssignedFlowForm;
import cz.metacentrum.registrar.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowFormRepository extends JpaRepository<AssignedFlowForm, Long> {
	List<AssignedFlowForm> getAllByMainForm(Form mainForm);

}
