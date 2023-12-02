package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.model.ApprovalGroup;
import cz.metacentrum.registrar.model.AssignedFlowForm;
import cz.metacentrum.registrar.model.AssignedFormModule;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormItem;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface FormService {

	Optional<Form> getFormById(Long id);
	List<Form> getFormsByIds(@NonNull Set<Long> ids);
	List<Form> getAllForms();
	Form createForm(Form form);
	void deleteForm(Long id);
	Form updateForm(Form form);

	List<FormItem> getFormItems(Long formId);

	List<FormItem> setFormItems(Long formId, List<FormItem> formItems);

	List<Form> getFormsByIdmObject(UUID idmObject);

	List<Long> getFormsByIdmManagersGroups(Set<UUID> groupUUIDs);
	List<Long> getFormsByIdmApprovalGroups(Set<UUID> groupUUIDs);

	List<AssignedFlowForm> getAssignedFlowForms(Long mainFormId);
	List<AssignedFlowForm> setAssignedFlowForms(Long mainFormId, List<AssignedFlowForm> assignedFlowForms);

	Optional<Form> getFormByUrlSuffix(String urlSuffix);

	List<AssignedFormModule> getAssignedModules(Long formId);

	List<AssignedFormModule> setAssignedModules(Long formId, List<AssignedFormModule> modules);

	List<ApprovalGroup> getApprovalGroups(Long formId);

	List<ApprovalGroup> setApprovalGroups(Long formId, List<ApprovalGroup> groups);
}
