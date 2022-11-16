package cz.metacentrum.registrarpersistence.repository;

import cz.metacentrum.registrarpersistence.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
}
