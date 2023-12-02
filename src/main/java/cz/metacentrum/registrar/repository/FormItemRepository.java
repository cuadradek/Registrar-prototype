package cz.metacentrum.registrar.repository;

import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FormItemRepository extends JpaRepository<FormItem, Long> {
	List<FormItem> getAllByFormAndIsDeleted(Form form, boolean deleted);
}
