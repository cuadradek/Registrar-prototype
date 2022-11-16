package cz.metacentrum.registrar.persistence.repository;

import cz.metacentrum.registrar.persistence.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
}
