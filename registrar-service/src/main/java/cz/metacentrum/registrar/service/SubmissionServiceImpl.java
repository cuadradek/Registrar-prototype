package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Approval;
import cz.metacentrum.registrar.persistence.entity.ApprovalGroup;
import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.persistence.entity.FormItemData;
import cz.metacentrum.registrar.persistence.entity.FormModule;
import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.persistence.entity.SubmissionResult;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import cz.metacentrum.registrar.persistence.repository.ApprovalRepository;
import cz.metacentrum.registrar.persistence.repository.FormItemRepository;
import cz.metacentrum.registrar.persistence.repository.SubmissionRepository;
import cz.metacentrum.registrar.persistence.repository.SubmittedFormRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class SubmissionServiceImpl implements SubmissionService {

	private final SubmittedFormRepository submittedFormRepository;
	private final SubmissionRepository submissionRepository;
	private final FormItemRepository formItemRepository;
	private final ApprovalRepository approvalRepository;
	private final FormService formService;
	private final ApplicationContext context;
	private static final Set<Form.FormState> OPEN_FORM_STATES = Set.of(Form.FormState.SUBMITTED, Form.FormState.VERIFIED);

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

	private SubmissionService getSelf() {
		return context.getBean(SubmissionService.class);
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
	public SubmissionResult createSubmission(Submission submission) {
		submission.getSubmittedForms().forEach(s -> {
			s.setFormState(Form.FormState.SUBMITTED);
			s.getFormData().forEach(d -> d.setFormItem(formItemRepository.getReferenceById(d.getFormItem().getId())));
		});
		submission.setTimestamp(LocalDateTime.now());
		//TODO fill the data like extSourceName, submittedBy...
		//TODO check if all the required fields are submitted, if all data belong to that form

		var flowForms = getAssignedFlowForms(submission);
		var autoFlowForms = flowForms.stream()
				.filter(a -> a.getFlowType() == AssignedFlowForm.FlowType.AUTO)
				.collect(Collectors.toMap(AssignedFlowForm::getFlowForm, Function.identity()));
		var redirectFlowForms = flowForms.stream()
				.filter(a -> a.getFlowType() == AssignedFlowForm.FlowType.REDIRECT)
				.map(AssignedFlowForm::getFlowForm)
				.collect(Collectors.toSet());

		autoFlowForms.values().forEach(a -> getSelf().submitAutoForm(submission, a));

		Submission saved = submissionRepository.save(submission);
		SubmissionResult result = new SubmissionResult();
		result.setSubmission(saved);
		if (!redirectFlowForms.isEmpty()) {
			try {
				Submission redirectSubmission = loadSubmission(redirectFlowForms);
				result.setRedirectSubmission(redirectSubmission);
			} catch (Exception ex) {
				String message = "Error during redirecting.";
				log.error(message, ex);
				result.addMessage(message);
			}
		}
		result.addMessage("Successfully submitted"); // todo use this default only if custom form message is missing
		return result;
	}

	@Override
	@Async
	public void submitAutoForm(Submission submission, AssignedFlowForm a) {
		Submission autoSubmission = loadSubmission(a.getFlowForm());
		autoSubmission.setSubmittedById(submission.getSubmittedById());
		autoSubmission.setSubmittedByName(submission.getSubmittedByName());
		autoSubmission.getSubmittedForms().forEach(s -> s.setSubmission(autoSubmission));
		//todo try to fill values
		createSubmission(autoSubmission);
	}

	private List<AssignedFlowForm> getAssignedFlowForms(Submission submission) {
		return submission.getSubmittedForms().stream()
				.flatMap(s -> formService.getAssignedFlowForms(s.getForm().getId())
						.stream()
						.filter(f -> f.getIfMainFlowType().contains(s.getFormType())))
				.collect(Collectors.toList());
	}

	@Override
	public SubmittedForm approveSubmittedForm(Long id) {
		SubmittedForm saved = submittedFormRepository.findById(id).orElseThrow();
		List<ApprovalGroup> principalsApprovalGroups = getPrincipalsApprovalGroups(saved);

		if (saved.getFormState() != Form.FormState.SUBMITTED) {
			throw new IllegalArgumentException("Form needs to be in SUBMITTED state!");
		}

		createApprovals(saved, principalsApprovalGroups, Approval.Decision.APPROVED, null);

		var modules = getModules(saved.getForm());
		modules.forEach(assignedModule -> assignedModule.getFormModule().beforeApprove(saved));

		if (tryToApprove(saved, principalsApprovalGroups)) {
			saved.setFormState(Form.FormState.APPROVED);
			modules.forEach(assignedModule -> assignedModule.getFormModule().onApprove(saved, assignedModule.getConfigOptions()));
			return submittedFormRepository.save(saved);
		}

		return saved;
	}

	private List<ApprovalGroup> getPrincipalsApprovalGroups(SubmittedForm saved) {
		Set<UUID> idmGroups = new HashSet<>();//todo these are from Principal object
		idmGroups.add(saved.getForm().getApprovalGroups().get(0).getIdmGroup());//todo remove this
		var principalsApprovalGroups = saved.getForm()
				.getApprovalGroups()
				.stream()
				.filter(g -> idmGroups.contains(g.getIdmGroup()))
				.collect(Collectors.toList());
		if (principalsApprovalGroups.isEmpty()) {
			throw new IllegalArgumentException("You are not authorized to approve this form!");
		}
		return principalsApprovalGroups;
	}

	private boolean tryToApprove(SubmittedForm saved, List<ApprovalGroup> approvalGroups) {
		var lastApprovalGroup = saved.getForm().getApprovalGroups()
				.stream()
				.sorted(Comparator.reverseOrder())
				.findFirst()
				.get();
		var approvalsCount = approvalRepository.findApprovalByLevelAndSubmittedFormAndDecision(
				lastApprovalGroup.getLevel(), saved, Approval.Decision.APPROVED).size();
		return lastApprovalGroup.getMinApprovals() <= approvalsCount;
	}

	private void createApprovals(SubmittedForm saved, List<ApprovalGroup> approvalGroups, Approval.Decision decision, @Nullable String message) {
		//todo get principal's mfa, get id, name
		boolean principalMfa = false;
		String principalId = "defaultId";
		String principalName = "defaultName";
		approvalGroups.forEach(approvalGroup -> {
			if (approvalGroup.isMfaRequired() && !principalMfa) {
				throw new IllegalArgumentException("You need to be authenticated using multi-factor authentication to approve or reject this form!");
			}
			Approval approval = new Approval(null, approvalGroup.getLevel(), principalMfa, saved, decision,
					principalId, principalName, LocalDateTime.now(), message);
			approvalRepository.save(approval);
			}
		);
	}

	@Override
	public SubmittedForm rejectSubmittedForm(Long id, String message) {
		SubmittedForm saved = submittedFormRepository.findById(id).orElseThrow();
		List<ApprovalGroup> principalsApprovalGroups = getPrincipalsApprovalGroups(saved);

		if (saved.getFormState() != Form.FormState.SUBMITTED) {
			throw new IllegalArgumentException("Form needs to be in SUBMITTED state!");
		}

		createApprovals(saved, principalsApprovalGroups, Approval.Decision.REJECTED, message);

		var modules = getModules(saved.getForm());
		modules.forEach(assignedModule -> assignedModule.getFormModule().onReject(saved));

		saved.setFormState(Form.FormState.REJECTED);
		return submittedFormRepository.save(saved);
	}

	private List<AssignedFormModule> getModules(Form form) {
		return form.getAssignedModules()
				.stream()
				.sorted()
				.map(this::setModule)
				.toList();
	}

	private AssignedFormModule setModule(AssignedFormModule assignedModule) {
		try {
			FormModule formModule = context.getBean(assignedModule.getModuleName(), FormModule.class);
			assignedModule.setFormModule(formModule);
			return assignedModule;
		} catch (BeansException ex) {
			throw new IllegalArgumentException("Non existing form module: " + assignedModule.getModuleName());
		}
	}

	@Override
	public Submission loadSubmission(Collection<Form> forms) {
		List<SubmittedForm> submittedForms = forms.stream()
				.map(this::loadSubmittedForm)
				.flatMap(List::stream)
				.collect(Collectors.toList());

		Submission submission = new Submission();
		submission.setSubmittedForms(submittedForms);
		return submission;
	}

	@Override
	public Submission loadSubmission(Form form) {
		return loadSubmission(List.of(form));
	}

	@Override
	public List<SubmittedForm> loadSubmittedForm(Form form) {
		SubmittedForm submittedForm = new SubmittedForm();
		submittedForm.setForm(form);
		boolean isOpen = submittedFormRepository.findSubmittedFormsByForm(form)
				.stream()
				.anyMatch(s -> OPEN_FORM_STATES.contains(s.getFormState()));
		if (isOpen) {
			throw new IllegalArgumentException("There is already submitted form for form + " + form.getId());
		}

		submittedForm.setFormType(Form.FormType.INITIAL);
		var modules = getModules(form);
		modules.forEach(m -> m.getFormModule().onLoad(submittedForm, m.getConfigOptions()));

		List<FormItem> items = formService.getFormItems(form.getId());
		List<FormItemData> itemDataList = items
				.stream()
				// TODO: prefill values
				.map(formItem -> new FormItemData(null, formItem, formItem.getShortname(), null, null, null, false))
				.toList();
		submittedForm.setFormData(itemDataList);

		List<SubmittedForm> submittedForms = new ArrayList<>();
		submittedForms.add(submittedForm);
		return submittedForms;
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
