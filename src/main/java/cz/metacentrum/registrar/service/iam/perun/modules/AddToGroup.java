package cz.metacentrum.registrar.service.iam.perun.modules;

import cz.metacentrum.perun.openapi.model.Group;
import cz.metacentrum.perun.openapi.model.Member;
import cz.metacentrum.perun.openapi.model.User;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.SubmittedForm;
import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.security.PrincipalService;
import cz.metacentrum.registrar.service.SubmissionService;
import cz.metacentrum.registrar.service.iam.perun.model.MemberHttp;
import cz.metacentrum.registrar.service.iam.perun.client.PerunEnhancedRPC;
import cz.metacentrum.registrar.service.iam.perun.client.PerunHttp;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class AddToGroup extends PerunFormModule {

	private static final String GROUP = "GROUP";
	private final FormService formService;
	private final SubmissionService submissionService;
	private final PrincipalService principalService;

	public AddToGroup(PerunHttp perunHttp, PerunEnhancedRPC perunRPC, FormService formService, SubmissionService submissionService, PrincipalService principalService) {
		super(perunHttp, perunRPC);
		this.formService = formService;
		this.submissionService = submissionService;
		this.principalService = principalService;
	}

	@Override
	public List<String> getConfigOptions() {
		return List.of("GROUP");
	}

	@Override
	public void beforeApprove(SubmittedForm submittedForm) {
	}

	@Override
	public void onApprove(SubmittedForm submittedForm, Map<String, String> configOptions) {
		Optional<User> user = perunRPC.getUserByIdentifier(submittedForm.getSubmission().getSubmitterId());
		if (user.isEmpty()) {

		}
		Group group = perunRPC.getGroupsManager().getGroupById(Integer.parseInt(configOptions.get(GROUP)));
		Member member = perunRPC.getMembersManager().getMemberByUser(group.getVoId(), user.get().getId());
		// TODO: getCurrentUser, getVoByUUID
		if (submittedForm.getFormType() == Form.FormType.INITIAL) {
//			groupsManager.addMember(group, member);
		} else {
			//extend
		}
	}

	@Override
	public void onReject(SubmittedForm submittedForm) {

	}

	@Override
	public List<SubmittedForm> onLoad(SubmittedForm submittedForm, Map<String, String> configOptions) {
		List<SubmittedForm> loadedForms = new ArrayList<>();
		Optional<User> user = perunRPC.getUserByIdentifier(principalService.getPrincipal().getId());
		MemberHttp member = null;
		if (user.isEmpty()) {
			submittedForm.setFormType(Form.FormType.INITIAL);
		} else {
			member = perunHttp.getMemberByUserAndVo(user.get().getId(), Integer.parseInt(configOptions.get(GROUP)));
			if (member == null) {
				submittedForm.setFormType(Form.FormType.INITIAL);
			} else {
				//check this call - if he is not in this group, then INITIAL
				//groupsManager.getMemberGroups(registrarSession, m);
				submittedForm.setFormType(Form.FormType.INITIAL);
				// if he is in the group check this call to see if he can EXTEND
//				groupsManager.canExtendMembershipInGroupWithReason(sess, member, group);
				submittedForm.setFormType(Form.FormType.EXTENSION);
			}
		}

		if (user == null || member == null) {
			//get VO and its UUID
			var forms = formService.getFormsByIdmObject(UUID.randomUUID());
			var voForm = forms.stream().filter(
					f -> formService.getAssignedModules(f).stream().anyMatch(
							a -> a.getModuleName().equals("addToVo") && a.getConfigOptions().get("VO").equals("VO_ID_TODO")
					)).findFirst();
			voForm.ifPresent(form -> loadedForms.addAll(submissionService.loadSubmittedForm(form)));
		}

		loadedForms.add(submittedForm);
		return loadedForms;
	}

	@Override
	public boolean hasRightToAddToForm(Form form, Map<String, String> configOptions) {
		return false;
	}
}
