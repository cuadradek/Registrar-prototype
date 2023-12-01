package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.persistence.entity.Approval;
import cz.metacentrum.registrar.persistence.entity.ApprovalGroup;
import cz.metacentrum.registrar.persistence.entity.AssignedFlowForm;
import cz.metacentrum.registrar.persistence.entity.AssignedFormModule;
import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItem;
import cz.metacentrum.registrar.persistence.entity.FormItemData;
import cz.metacentrum.registrar.persistence.entity.FormModule;
import cz.metacentrum.registrar.persistence.entity.FormState;
import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.persistence.entity.SubmissionResult;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import cz.metacentrum.registrar.persistence.repository.ApprovalRepository;
import cz.metacentrum.registrar.persistence.repository.FormItemRepository;
import cz.metacentrum.registrar.persistence.repository.SubmissionRepository;
import cz.metacentrum.registrar.persistence.repository.SubmittedFormRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Objects;
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
	private final PrincipalService principalService;
	private final IamService iamService;

	@Value("${registrar.find-similar-users}")
	private boolean findSimilarUsers;

	@Autowired
	public SubmissionServiceImpl(SubmittedFormRepository submittedFormRepository, SubmissionRepository submissionRepository, FormItemRepository formItemRepository, ApprovalRepository approvalRepository, FormService formService, ApplicationContext context, PrincipalService principalService, IamService iamService) {
		this.submittedFormRepository = submittedFormRepository;
		this.submissionRepository = submissionRepository;
		this.formItemRepository = formItemRepository;
		this.approvalRepository = approvalRepository;
		this.formService = formService;
		this.context = context;
		this.principalService = principalService;
		this.iamService = iamService;
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
	public List<SubmittedForm> findSubmittedFormsByFormAndState(Form form, FormState state) {
		return submittedFormRepository.findSubmittedFormsByFormAndFormState(form, state);
	}

	private void checkFilledItemData(FormItemData itemData, RegistrarPrincipal principal, Form form) {
		var formItem = formItemRepository.findById(itemData.getFormItem().getId()).orElseThrow();
		itemData.setFormItem(formItem);

		if (formItem.isRequired() && StringUtils.isEmpty(itemData.getValue())) {
			throw new IllegalArgumentException(String.format("Form item with id: %d is required but not filled out!", formItem.getId()));
		}
		//todo check regex

		if (!formItem.getForm().getId().equals(form.getId())) {
			throw new IllegalArgumentException(String.format("Form item with id: %d belongs to form with id: %d instead of %d!",
					formItem.getId(), formItem.getForm().getId(), form.getId()));
		}

		if (!principal.isAuthenticated()) {
			itemData.setAssuranceLevel(0);
			return;
		}

		var iamValue = getIamAttributeValue(formItem, principal);
		var identityValue = getIdentityAttributeValue(formItem, principal);
		itemData.setIamPrefilledValue(iamValue);
		itemData.setIdentityPrefilledValue(identityValue);

		if (StringUtils.isEmpty(itemData.getValue())) {
			itemData.setAssuranceLevel(0);
		} else if (Objects.equals(identityValue, itemData.getValue()) || Objects.equals(iamValue, itemData.getValue())) {
			itemData.setAssuranceLevel(2);
		} else {
			itemData.setAssuranceLevel(0);
		}
	}

	@Override
	public SubmissionResult createSubmission(Submission submission) {
		RegistrarPrincipal principal = principalService.getPrincipal();
		submission.getSubmittedForms().forEach(s -> {
			s.setFormState(FormState.PENDING_VERIFICATION);
			s.getFormData().forEach(d -> checkFilledItemData(d, principal, s.getForm()));
			checkAllRequiredItemsAreFilled(s);
		});

		submission.setTimestamp(LocalDateTime.now());
		submission.setSubmitterId(principal.getId());
		submission.setSubmitterName(principal.getName());

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

	private void checkAllRequiredItemsAreFilled(SubmittedForm s) {
		var requiredItemsIds = formItemRepository.getAllByFormAndIsDeleted(s.getForm(), false)
				.stream()
				.filter(FormItem::isRequired)
				.map(FormItem::getId)
				.collect(Collectors.toList());
		var filledItemsIds = s.getFormData()
				.stream()
				.filter(d -> StringUtils.isEmpty(d.getValue()))
				.map(d -> d.getFormItem().getId())
				.collect(Collectors.toList());
		if (requiredItemsIds.containsAll(filledItemsIds)) {
			throw new IllegalArgumentException("Missing required items: " + requiredItemsIds.removeAll(filledItemsIds));
		}
	}

	@Override
	@Async
	public void submitAutoForm(Submission submission, AssignedFlowForm a) {
		Submission autoSubmission = loadSubmission(a.getFlowForm(), null);
		autoSubmission.setSubmitterId(submission.getSubmitterId());
		autoSubmission.setSubmitterName(submission.getSubmitterName());
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
	public SubmittedForm makeApprovalDecision(SubmittedForm submittedForm, Approval.Decision decision, String message) {
		List<ApprovalGroup> principalsApprovalGroups = getPrincipalsApprovalGroups(submittedForm);

		if (submittedForm.getFormState().canMakeDecision()) {
			throw new IllegalArgumentException("Form needs to be in of the following states: "
					+ FormState.DECISION_POSSIBLE_STATES);
		}

		createApprovals(submittedForm, principalsApprovalGroups, Approval.Decision.APPROVED, message);
		var modules = getModules(submittedForm.getForm());

		switch (decision) {
			case APPROVED: approveForm(submittedForm, modules, principalsApprovalGroups);
			case REJECTED: rejectSubmittedForm(submittedForm, modules);
			case CHANGES_REQUESTED: requestChanges(submittedForm);
		}

		return submittedForm;
	}

	private SubmittedForm approveForm(SubmittedForm submittedForm, List<AssignedFormModule> modules, List<ApprovalGroup> principalsApprovalGroups) {
		modules.forEach(assignedModule -> assignedModule.getFormModule().beforeApprove(submittedForm));

		if (tryToApprove(submittedForm, principalsApprovalGroups)) {
			submittedForm.setFormState(FormState.APPROVED);
			modules.forEach(assignedModule -> assignedModule.getFormModule().onApprove(submittedForm, assignedModule.getConfigOptions()));
			return submittedFormRepository.save(submittedForm);
		}

		return submittedForm;
	}

	private SubmittedForm rejectSubmittedForm(SubmittedForm submittedForm, List<AssignedFormModule> modules) {
		modules.forEach(assignedModule -> assignedModule.getFormModule().onReject(submittedForm));

		submittedForm.setFormState(FormState.REJECTED);
		return submittedFormRepository.save(submittedForm);
	}

	private SubmittedForm requestChanges(SubmittedForm submittedForm) {
		submittedForm.setFormState(FormState.CHANGES_REQUESTED);
		return submittedFormRepository.save(submittedForm);
	}

	private List<ApprovalGroup> getPrincipalsApprovalGroups(SubmittedForm saved) {
		Set<UUID> idmGroups = new HashSet<>();//todo these are from Principal object
		List<ApprovalGroup> approvalGroups = formService.getApprovalGroups(saved.getForm().getId());
		idmGroups.add(approvalGroups.get(0).getIamGroup());//todo remove this
		var principalsApprovalGroups = approvalGroups
				.stream()
				.filter(g -> idmGroups.contains(g.getIamGroup()))
				.collect(Collectors.toList());
		if (principalsApprovalGroups.isEmpty()) {
			throw new IllegalArgumentException("You are not authorized to approve this form!");
		}
		return principalsApprovalGroups;
	}

	private boolean tryToApprove(SubmittedForm saved, List<ApprovalGroup> approvalGroups) {
		var lastApprovalGroup = formService.getApprovalGroups(saved.getForm().getId())
				.stream()
				.sorted(Comparator.reverseOrder())
				.findFirst()
				.get();
		var approvalsCount = approvalRepository.findApprovalByLevelAndSubmittedFormAndDecision(
				lastApprovalGroup.getLevel(), saved, Approval.Decision.APPROVED).size();
		return lastApprovalGroup.getMinApprovals() <= approvalsCount;
	}

	private void createApprovals(SubmittedForm saved, List<ApprovalGroup> approvalGroups, Approval.Decision decision, @Nullable String message) {
		RegistrarPrincipal principal = principalService.getPrincipal();
		approvalGroups.forEach(approvalGroup -> {
			if (approvalGroup.isMfaRequired() && !principal.isMfa()) {
				throw new IllegalArgumentException("You need to be authenticated using multi-factor authentication to approve or reject this form!");
			}
			Approval approval = new Approval(null, approvalGroup.getLevel(), principal.isMfa(), saved, decision,
					principal.getId(), principal.getName(), LocalDateTime.now(), message);
			approvalRepository.save(approval);
			}
		);
	}

	@Override
	public SubmittedForm rejectSubmittedForm(Long id, String message) {
		SubmittedForm saved = submittedFormRepository.findById(id).orElseThrow();
		List<ApprovalGroup> principalsApprovalGroups = getPrincipalsApprovalGroups(saved);

		if (saved.getFormState().canMakeDecision()) {
			throw new IllegalArgumentException("Form needs to be in of the following states: "
					+ FormState.DECISION_POSSIBLE_STATES);
		}

		createApprovals(saved, principalsApprovalGroups, Approval.Decision.REJECTED, message);

		var modules = getModules(saved.getForm());
		modules.forEach(assignedModule -> assignedModule.getFormModule().onReject(saved));

		saved.setFormState(FormState.REJECTED);
		return submittedFormRepository.save(saved);
	}

	private List<AssignedFormModule> getModules(Form form) {
		return formService.getAssignedModules(form.getId())
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

		RegistrarPrincipal principal = principalService.getPrincipal();
		if (findSimilarUsers && principal.isAuthenticated()) {
			submission.setSimilarUsers(iamService.getSimilarUsers(principal.getAttributes()));
		}
		return submission;
	}

	@Override
	public Submission loadSubmission(Form form, String redirectUrl) {
		Submission submission = loadSubmission(List.of(form));
		submission.getSubmittedForms().stream()
				.filter(s -> s.getForm().getId().equals(form.getId()))
				.findFirst()
				.orElseThrow()
				.setRedirectUrl(redirectUrl);
		return submission;
	}

	@Override
	public List<SubmittedForm> loadSubmittedForm(Form form) {
		SubmittedForm submittedForm = new SubmittedForm();
		submittedForm.setForm(form);
		boolean isOpen = submittedFormRepository.findSubmittedFormsByForm(form)
				.stream()
				.anyMatch(s -> s.getFormState().isOpenFormState());
		if (isOpen) {
			throw new IllegalArgumentException("There is already submitted form for form + " + form.getId());
		}

		submittedForm.setFormType(Form.FormType.INITIAL);
		var modules = getModules(form);
		modules.forEach(m -> m.getFormModule().onLoad(submittedForm, m.getConfigOptions()));

		List<FormItem> items = formService.getFormItems(form.getId());
		var principal = principalService.getPrincipal();
		List<FormItemData> itemDataList = items
				.stream()
				.map(item -> prefillValue(item, principal))
				.toList();
		submittedForm.setFormData(itemDataList);

		List<SubmittedForm> submittedForms = new ArrayList<>();
		submittedForms.add(submittedForm);
		return submittedForms;
	}

	private FormItemData prefillValue(FormItem formItem, RegistrarPrincipal principal) {
		var itemData = new FormItemData(null, formItem, null, null, null, null);

		var staticValue = formItem.getPrefilledValue();

		if (principal.isAuthenticated()) {
			if (staticValue != null) {
				itemData.setValue(staticValue);
			}
			return itemData;
		}

		String prefilledValue = null;
		var identityValue = getIdentityAttributeValue(formItem, principal);

		if (formItem.isPreferIdentityAttribute() && identityValue != null) {
			prefilledValue = identityValue;
		} else {
			var iamValue = getIamAttributeValue(formItem, principal);
			if (iamValue != null) {
				prefilledValue = iamValue;
			} else if (identityValue != null) {
				prefilledValue = identityValue;
			} else if (staticValue != null) {
				prefilledValue = staticValue;
			}
		}

		itemData.setValue(prefilledValue);
		return itemData;
	}

	private @Nullable String getIdentityAttributeValue(FormItem formItem, RegistrarPrincipal principal) {
		var identityAttribute = formItem.getSourceIdentityAttribute();
		return identityAttribute == null ? null : principal.getClaimAsString(identityAttribute);
	}

	private @Nullable String getIamAttributeValue(FormItem formItem, RegistrarPrincipal principal) {
		var attribute = formItem.getIamSourceAttribute();
		return attribute == null ? null : iamService.getUserAttributeValue(principal.getId(), attribute);
	}


	@Override
	public List<SubmittedForm> getSubmittedFormsBySubmitterId(String submitterId) {
		return submissionRepository
				.getAllBySubmitterId(submitterId)
				.stream()
				.map(Submission::getSubmittedForms)
				.flatMap(List::stream)
				.collect(Collectors.toList());
	}
}
