package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Approval;
import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.persistence.entity.FormItemData;
import cz.metacentrum.registrar.persistence.entity.FormModule;
import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import cz.metacentrum.registrar.persistence.repository.ApprovalRepository;
import cz.metacentrum.registrar.persistence.repository.FormItemRepository;
import cz.metacentrum.registrar.persistence.repository.SubmissionRepository;
import cz.metacentrum.registrar.persistence.repository.SubmittedFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubmissionServiceImpl implements SubmissionService {

	private final SubmittedFormRepository submittedFormRepository;
	private final SubmissionRepository submissionRepository;
	private final FormItemRepository formItemRepository;
	private final ApprovalRepository approvalRepository;
	private final FormService formService;
	private final ApplicationContext context;

	// TODO change path based on IDM used
	private static final String MODULE_PACKAGE_PATH = "cz.metacentrum.registrar.service.idm.perun.modules.";

	@Autowired
	public SubmissionServiceImpl(SubmittedFormRepository submittedFormRepository, SubmissionRepository submissionRepository, FormItemRepository formItemRepository, ApprovalRepository approvalRepository, FormService formService, ApplicationContext context) {
		this.submittedFormRepository = submittedFormRepository;
		this.submissionRepository = submissionRepository;
		this.formItemRepository = formItemRepository;
		this.approvalRepository = approvalRepository;
		this.formService = formService;
		this.context = context;
	}

	@Override
	public Optional<Submission> findSubmissionById(Long id) {
		return submissionRepository.findById(id);
	}

	@Override
	public Optional<SubmittedForm> findSubmittedFormById(Long id) {
		return submittedFormRepository.findById(id);
	}

	@Override
	public List<SubmittedForm> findSubmittedFormsByForm(Form form) {
		return submittedFormRepository.findSubmittedFormsByForm(form);
	}

	@Override
	public List<SubmittedForm> findSubmittedFormsByFormAndState(Form form, Form.FormState state) {
		return submittedFormRepository.findSubmittedFormsByFormAndFormState(form, state);
	}

	@Override
	public Submission createSubmission(Submission submission) {
		submission.getSubmittedForms().forEach(s -> {
			s.setFormState(Form.FormState.SUBMITTED);
			s.getFormData().forEach(d -> d.setFormItem(formItemRepository.getReferenceById(d.getFormItem().getId())));
		});
		submission.setTimestamp(LocalDateTime.now());
		//TODO fill the data like extSourceName, submittedBy...
		//TODO check if all the required fields are submitted, if all data belong to that form
		return submissionRepository.save(submission);
	}

	@Override
	@Transactional
	public SubmittedForm approveSubmittedForm(Long id) {
		//TODO: check if have rights to approve
		SubmittedForm saved = submittedFormRepository.findById(id).orElseThrow();
		if (saved.getFormState() != Form.FormState.SUBMITTED) {
			// TODO throw exception
		}
		Approval approval = new Approval(null, 0, false, saved, Approval.Decision.APPROVED,
				"todo-id-of-approver", "todo-name-of-approver", LocalDateTime.now(), null);
		approvalRepository.save(approval);
		//TODO: beforeApprove of modules
		saved.setFormState(Form.FormState.APPROVED);
		//TODO: onApprove of modules
		var modules = saved.getForm().getAssignedModules()
				.stream()
				.sorted()
				.toList();
		modules.forEach(assignedModule -> getModule(assignedModule).onApprove(saved));
		return submittedFormRepository.save(saved);
		//TODO: send notifications
	}

	private FormModule getModule(AssignedFormModule assignedModule) {
		return context.getBean(assignedModule.getModuleName(), FormModule.class);
//		try {
//			return (FormModule) Class.forName(MODULE_PACKAGE_PATH + assignedModule.getModuleName()).getConstructor().newInstance();
//		} catch (InstantiationException | ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
//			throw new IllegalArgumentException("Non existing form module: " + assignedModule.getModuleName());
//		}
	}

	@Override
	public SubmittedForm loadSubmission(Long formId) {
		Form form = formService.getFormById(formId).orElseThrow();
		SubmittedForm submittedForm = new SubmittedForm();
		submittedForm.setForm(form);
		// TODO: determine whether we want to load INITIAL or EXTENSION?
		submittedForm.setFormType(Form.FormType.INITIAL);
		List<FormItem> items = formService.getFormItems(formId);
		List<FormItemData> itemDataList = items
				.stream()
				// TODO: prefill values
				.map(formItem -> new FormItemData(null, formItem, formItem.getShortname(), null, null, null, false))
				.toList();
		submittedForm.setFormData(itemDataList);
		return submittedForm;
	}

	@Override
	public List<SubmittedForm> getSubmittedFormsBySubmitterId(String submitterId) {
		return submissionRepository
				.getAllBySubmittedById(submitterId)
				.stream()
				.map(Submission::getSubmittedForms)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
}
