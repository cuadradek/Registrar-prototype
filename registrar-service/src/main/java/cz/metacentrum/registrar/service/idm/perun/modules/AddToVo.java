package cz.metacentrum.registrar.service.idm.perun.modules;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import cz.metacentrum.registrar.service.PrincipalService;
import cz.metacentrum.registrar.service.idm.perun.Member;
import cz.metacentrum.registrar.service.idm.perun.PerunHttp;
import cz.metacentrum.registrar.service.idm.perun.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AddToVo extends PerunFormModule {

	private static final String VO = "VO";
	private final PrincipalService principalService;

	public AddToVo(PerunHttp perunHttp, PrincipalService principalService) {
		super(perunHttp);
		this.principalService = principalService;
	}

	@Override
	public Map<String, String> getConfigOptions() {
		return Map.of(VO, "");
	}

	@Override
	public SubmittedForm beforeApprove(SubmittedForm submittedForm) {
		return submittedForm;
	}

	@Override
	public SubmittedForm onApprove(SubmittedForm submittedForm, Map<String, String> configOptions) {
		User user = perunHttp.getUserByIdentifier(submittedForm.getSubmission().getSubmittedById());
		if (user == null) {
			//create him
		}
		if (submittedForm.getFormType() == Form.FormType.INITIAL) {
			perunHttp.createMember(user.getId(), Integer.parseInt(configOptions.get(VO)));
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
		User user = perunHttp.getUserByIdentifier(principalService.getPrincipal().getId());
		if (user == null) {
			submittedForm.setFormType(Form.FormType.INITIAL);
		} else {
			Member member = perunHttp.getMemberByUserAndVo(user.getId(), Integer.parseInt(configOptions.get(VO)));
			if (member == null) {
				submittedForm.setFormType(Form.FormType.INITIAL);
			} else {
				// check this call to see if he can EXTEND
//				membersManager.canExtendMembershipWithReason(member);
				submittedForm.setFormType(Form.FormType.EXTENSION);
			}
		}
		return List.of(submittedForm);
	}
}
