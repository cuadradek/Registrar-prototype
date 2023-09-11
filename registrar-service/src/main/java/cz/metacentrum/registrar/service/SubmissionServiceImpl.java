package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.persistence.entity.FormItemData;
import cz.metacentrum.registrar.persistence.entity.FormModule;
import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.persistence.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

	private final SubmissionRepository submissionRepository;
	private final FormService formService;
	private final ApplicationContext context;

	// TODO change path based on IDM used
	private static final String MODULE_PACKAGE_PATH = "cz.metacentrum.registrar.service.idm.perun.modules.";

	@Autowired
	public SubmissionServiceImpl(SubmissionRepository submissionRepository, FormService formService, ApplicationContext context) {
		this.submissionRepository = submissionRepository;
		this.formService = formService;
		this.context = context;
	}

	@Override
	public Optional<Submission> findSubmissionById(Long id) {
		return submissionRepository.findById(id);
	}

	@Override
	public List<Submission> findSubmissionsByForm(Form form) {
		return submissionRepository.findSubmittedFormsByForm(form);
	}

	@Override
	public List<Submission> findSubmissionsByFormAndState(Form form, Form.FormState state) {
		return submissionRepository.findSubmittedFormsByFormAndFormState(form, state);
	}

	@Override
	public Submission createSubmission(Submission submission) {
		submission.setFormState(Form.FormState.SUBMITTED);
		//TODO fill the data like extSourceName, submittedBy...
		//TODO check if all the required fields are submitted
		return submissionRepository.save(submission);
	}

	@Override
	public Submission approveSubmission(Long id) {
		//TODO: check if have rights to approve
		Submission saved = submissionRepository.findById(id).orElseThrow();
		if (saved.getFormState() != Form.FormState.SUBMITTED) {
			// TODO throw exception
		}
		//TODO: beforeApprove of modules
		saved.setFormState(Form.FormState.APPROVED);
		//TODO: onApprove of modules
		var modules = saved.getForm().getAssignedModules()
				.stream()
				.sorted()
				.toList();
		modules.forEach(assignedModule -> getModule(assignedModule).onApprove(saved));
		return submissionRepository.save(saved);
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
	public Submission loadSubmission(Long formId) {
		Form form = formService.getFormById(formId).orElseThrow();
		Submission submission = new Submission();
		submission.setForm(form);
		// TODO: determine whether we want to load INITIAL or EXTENSION?
		submission.setFormType(Form.FormType.INITIAL);
		List<FormItem> items = formService.getFormItems(formId);
		List<FormItemData> itemDataList = items
				.stream()
				// TODO: prefill values
				.map(formItem -> new FormItemData(null, formItem, formItem.getShortname(), null, null, null, false))
				.toList();
		submission.setFormData(itemDataList);
		return submission;
	}
}
