package cz.metacentrum.registrar.repository;

import cz.metacentrum.registrar.model.AssignedFormModule;
import cz.metacentrum.registrar.model.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormModuleRepository extends JpaRepository<AssignedFormModule, Long> {
	List<AssignedFormModule> getAllByForm(Form form);

}
