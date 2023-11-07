package cz.metacentrum.registrar.persistence.repository;

import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import cz.metacentrum.registrar.persistence.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormModuleRepository extends JpaRepository<AssignedFormModule, Long> {
	List<AssignedFormModule> getAllByForm(Form form);

}
