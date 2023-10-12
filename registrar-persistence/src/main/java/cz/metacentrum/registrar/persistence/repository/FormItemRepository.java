package cz.metacentrum.registrar.persistence.repository;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FormItemRepository extends JpaRepository<FormItem, Long> {
	List<FormItem> getAllByFormAndIsDeleted(Form form, boolean deleted);
}
