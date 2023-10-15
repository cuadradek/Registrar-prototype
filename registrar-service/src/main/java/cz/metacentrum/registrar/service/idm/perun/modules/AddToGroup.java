package cz.metacentrum.registrar.service.idm.perun.modules;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import cz.metacentrum.registrar.service.FormService;
import cz.metacentrum.registrar.service.PrincipalService;
import cz.metacentrum.registrar.service.SubmissionService;
import cz.metacentrum.registrar.service.idm.perun.Member;
import cz.metacentrum.registrar.service.idm.perun.PerunHttp;
import cz.metacentrum.registrar.service.idm.perun.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AddToGroup extends PerunFormModule {

	private static final String GROUP = "GROUP";
	private final FormService formService;
	private final SubmissionService submissionService;
	private final PrincipalService principalService;

	public AddToGroup(PerunHttp perunHttp, FormService formService, SubmissionService submissionService, PrincipalService principalService) {
		super(perunHttp);
		this.formService = formService;
		this.submissionService = submissionService;
		this.principalService = principalService;
	}

	@Override
	public Map<String, String> getConfigOptions() {
		return Map.of("GROUP", "");
	}

	@Override
	public SubmittedForm beforeApprove(SubmittedForm submittedForm) {
		return submittedForm;
	}

	@Override
	public SubmittedForm onApprove(SubmittedForm submittedForm, Map<String, String> configOptions) {
		User user = perunHttp.getUserByIdentificator(principalService.getPrincipal().getId());
		Member member = perunHttp.getMemberByUserAndVo(user.getId(), Integer.parseInt(configOptions.get(GROUP)));
		// TODO: getCurrentUser, getVoByUUID
		if (submittedForm.getFormType() == Form.FormType.INITIAL) {
//			groupsManager.addMember(group, member);
		} else {
			//extend
		}
		return submittedForm;
	}

	@Override
	public SubmittedForm onReject(SubmittedForm submittedForm) {
		return submittedForm;
	}

	@Override
	public List<SubmittedForm> onLoad(SubmittedForm submittedForm, Map<String, String> configOptions) {
		List<SubmittedForm> loadedForms = new ArrayList<>();
		User user = perunHttp.getUserByIdentificator(principalService.getPrincipal().getId());
		Member member = null;
		if (user == null) {
			submittedForm.setFormType(Form.FormType.INITIAL);
		} else {
			member = perunHttp.getMemberByUserAndVo(user.getId(), Integer.parseInt(configOptions.get(GROUP)));
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
					f -> f.getAssignedModules().stream().anyMatch(
							a -> a.getModuleName().equals("addToVo") && a.getConfigOptions().get("VO").equals("VO_ID_TODO")
					)).findFirst();
			if (voForm.isPresent()) {
				loadedForms.addAll(submissionService.loadSubmittedForm(voForm.get()));
			}
		}

		loadedForms.add(submittedForm);
		return loadedForms;
	}
}
