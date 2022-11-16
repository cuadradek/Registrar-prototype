package cz.metacentrum.registrarpersistence.repository;

import cz.metacentrum.registrarpersistence.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, Long> {
}
