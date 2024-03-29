package cz.metacentrum.registrar.service;

import cz.metacentrum.registrar.model.Approval;
import cz.metacentrum.registrar.model.ApprovalGroup;
import cz.metacentrum.registrar.model.AssignedFlowForm;
import cz.metacentrum.registrar.model.AssignedFormModule;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.FormItem;
import cz.metacentrum.registrar.model.FormItemData;
import cz.metacentrum.registrar.security.PrincipalService;
import cz.metacentrum.registrar.security.RegistrarPrincipal;
import cz.metacentrum.registrar.model.FormState;
import cz.metacentrum.registrar.model.Submission;
import cz.metacentrum.registrar.model.SubmissionResult;
import cz.metacentrum.registrar.model.SubmittedForm;
import cz.metacentrum.registrar.repository.ApprovalRepository;
import cz.metacentrum.registrar.repository.FormItemRepository;
import cz.metacentrum.registrar.repository.SubmissionRepository;
import cz.metacentrum.registrar.repository.SubmittedFormRepository;
import cz.metacentrum.registrar.service.iam.IamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;
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

	@Value("#{'${registrar.oauth2.resourceserver.user-identifiers-claims}'.split(',')}")
	private List<String> userIdentifierClaims;

	@Value("${registrar.oauth2.resourceserver.original-idp-claim}")
	private String originalIdpClaimName;

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
			checkOpenSubmittedForms(s.getForm(), principal);
			s.setFormState(FormState.PENDING_APPROVAL);
			s.getFormData().forEach(d -> checkFilledItemData(d, principal, s.getForm()));
			checkAllRequiredItemsAreFilled(s);
		});

		setSubmissionMetadata(submission, principal);

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
		setRedirections(submission, redirectFlowForms, result);
		result.addMessage("Successfully submitted"); // todo use this default only if custom form message is missing
		return result;
	}

	private void setSubmissionMetadata(Submission submission, RegistrarPrincipal principal) {
		submission.setTimestamp(LocalDateTime.now());
		if (!principal.isAuthenticated()) {
			submission.setSubmitterId(null);
			submission.setSubmitterName(null);
			submission.setOriginalIdentityIssuer(null);
			submission.setOriginalIdentityIdentifier(null);
			submission.setOriginalIdentityLoa(0);
			submission.setIdentityAttributes(null);
			return;
		}

		if (iamService.userExists(principal.getId())) {
			submission.setSubmitterId(principal.getId());
		} else {
			submission.setSubmitterId(null);
		}

		submission.setSubmitterName(principal.getName());
		submission.setOriginalIdentityIssuer(principal.getClaimAsString(originalIdpClaimName));

		Optional<String> identifier = getOriginalIdentifier(principal);
		if (identifier.isPresent()) {
			submission.setOriginalIdentityIdentifier(identifier.get());
		} else {
			submission.setOriginalIdentityIdentifier(null);
			log.warn("No original identifier found for user {}", principal.getId());
		}

		submission.setOriginalIdentityLoa(1);
		submission.setIdentityAttributes(principal.getAttributes().entrySet().stream()
				.filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toString())));
	}

	private Optional<String> getOriginalIdentifier(RegistrarPrincipal principal) {
		return userIdentifierClaims.stream()
				.map(principal::getClaimAsString)
				.filter(StringUtils::isNotEmpty)
				.findFirst();
	}

	private void setRedirections(Submission submission, Set<Form> redirectFlowForms, SubmissionResult result) {
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
		submission.getSubmittedForms().stream()
				.map(SubmittedForm::getRedirectUrl)
				.filter(StringUtils::isNotEmpty)
				.findFirst()
				.ifPresent(result::setRedirectUrl);
	}

	private void checkAllRequiredItemsAreFilled(SubmittedForm s) {
		var requiredItemsIds = formItemRepository.getAllByFormAndIsDeleted(s.getForm(), false)
				.stream()
				.filter(FormItem::isRequired)
				.map(FormItem::getId)
				.collect(Collectors.toList());
		var filledItemsIds = s.getFormData()
				.stream()
				.filter(d -> !StringUtils.isEmpty(d.getValue()))
				.map(d -> d.getFormItem().getId())
				.collect(Collectors.toList());
		if (!filledItemsIds.containsAll(requiredItemsIds)) {
			requiredItemsIds.removeAll(filledItemsIds);
			throw new IllegalArgumentException("Missing required items: " + requiredItemsIds);
		}
	}

	@Override
	@Async
	public void submitAutoForm(Submission submission, AssignedFlowForm a) {
		Submission autoSubmission = loadSubmission(a.getFlowForm(), null);
		autoSubmission.setSubmitterId(submission.getSubmitterId());
		autoSubmission.setSubmitterName(submission.getSubmitterName());
		autoSubmission.setOriginalIdentityLoa(submission.getOriginalIdentityLoa());
		autoSubmission.setIdentityAttributes(submission.getIdentityAttributes());
		autoSubmission.setOriginalIdentityIssuer(submission.getOriginalIdentityIssuer());
		autoSubmission.setOriginalIdentityIdentifier(submission.getOriginalIdentityIdentifier());
		autoSubmission.getSubmittedForms().forEach(s -> s.setSubmission(autoSubmission));
		//todo try to fill values
		createSubmission(autoSubmission);
	}

	private List<AssignedFlowForm> getAssignedFlowForms(Submission submission) {
		return submission.getSubmittedForms().stream()
				.flatMap(s -> formService.getAssignedFlowForms(s.getForm())
						.stream()
						.filter(f -> f.getIfMainFlowType().contains(s.getFormType())))
				.collect(Collectors.toList());
	}

	@Override
	public void consolidateSubmissions(String submitterId, String originalIdentityIdentifier, String originalIdentityIssuer) {
		var submissions = submissionRepository.getAllByOriginalIdentityIdentifierAndOriginalIdentityIssuer(
				originalIdentityIdentifier, originalIdentityIssuer);
		submissions.forEach(s -> s.setSubmitterId(submitterId));
		submissionRepository.saveAll(submissions);
	}

	@Override
	public SubmittedForm createApproval(Approval approval) {
		SubmittedForm submittedForm = approval.getSubmittedForm();
		List<ApprovalGroup> principalsApprovalGroups = getPrincipalsApprovalGroups(submittedForm);

		if (!approval.getSubmittedForm().getFormState().canMakeDecision()) {
			throw new IllegalArgumentException("Form needs to be in of the following states: "
					+ FormState.DECISION_POSSIBLE_STATES);
		}

		createApprovals(principalsApprovalGroups, approval);
		var modules = formService.getAssignedModules(submittedForm.getForm());

		submittedForm = switch (approval.getDecision()) {
			case APPROVED -> approveForm(submittedForm, modules, principalsApprovalGroups);
			case REJECTED -> rejectSubmittedForm(submittedForm, modules);
			case CHANGES_REQUESTED -> requestChanges(submittedForm);
		};

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
		List<ApprovalGroup> approvalGroups = formService.getApprovalGroups(saved.getForm());
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
		var lastApprovalGroup = formService.getApprovalGroups(saved.getForm())
				.stream()
				.sorted(Comparator.reverseOrder())
				.findFirst()
				.get();
		var approvalsCount = approvalRepository.findApprovalByLevelAndSubmittedFormAndDecision(
				lastApprovalGroup.getLevel(), saved, Approval.Decision.APPROVED).size();
		return lastApprovalGroup.getMinApprovals() <= approvalsCount;
	}

	private void createApprovals(List<ApprovalGroup> approvalGroups, Approval approval) {
		RegistrarPrincipal principal = principalService.getPrincipal();
		approvalGroups.forEach(approvalGroup -> {
					if (approvalGroup.isMfaRequired() && !principal.isMfa()) {
						throw new IllegalArgumentException("You need to be authenticated using multi-factor authentication to approve or reject this form!");
					}
					approval.setLevel(approvalGroup.getLevel());
					approval.setMfa(principal.isMfa());
					approval.setApproverId(principal.getId());
					approval.setApproverName(principal.getName());
					approval.setTimestamp(LocalDateTime.now());
					approvalRepository.save(approval);
				}
		);
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
		var principal = principalService.getPrincipal();
		SubmittedForm submittedForm = new SubmittedForm();
		submittedForm.setForm(form);

		checkOpenSubmittedForms(form, principal);

		submittedForm.setFormType(Form.FormType.INITIAL);
		var modules = formService.getAssignedModules(form);
		modules.forEach(m -> m.getFormModule().onLoad(submittedForm, m.getConfigOptions()));

		List<FormItem> items = formService.getFormItems(form);
		List<FormItemData> itemDataList = items
				.stream()
				.map(item -> prefillValue(item, principal))
				.toList();
		submittedForm.setFormData(itemDataList);

		List<SubmittedForm> submittedForms = new ArrayList<>();
		submittedForms.add(submittedForm);
		return submittedForms;
	}

	private void checkOpenSubmittedForms(Form form, RegistrarPrincipal principal) {
		if (principal.isAuthenticated()) {
			boolean isOpen = submittedFormRepository.findSubmittedFormsByForm(form)
					.stream()
					.filter(s -> s.getFormState().isOpenFormState())
					.anyMatch(s ->
							//todo check also by original identifier + issuer
							(principal.getId() != null && principal.getId().equals(s.getSubmission().getSubmitterId())));
			if (isOpen) {
				throw new IllegalArgumentException("You already have open submitted form for form + " + form.getId());
			}
		}
	}

	private FormItemData prefillValue(FormItem formItem, RegistrarPrincipal principal) {
		var itemData = new FormItemData(null, formItem, null, null, null, null);

		var staticValue = formItem.getPrefilledStaticValue();

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
