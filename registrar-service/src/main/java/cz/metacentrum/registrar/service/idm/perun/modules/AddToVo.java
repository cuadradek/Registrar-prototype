package cz.metacentrum.registrar.service.idm.perun.modules;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import cz.metacentrum.registrar.service.idm.perun.Member;
import cz.metacentrum.registrar.service.idm.perun.PerunHttp;
import cz.metacentrum.registrar.service.idm.perun.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AddToVo extends PerunFormModule {

	private static final String VO = "VO";

	public AddToVo(PerunHttp perunHttp) {
		super(perunHttp);
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
		// TODO: getCurrentUser, getVoByUUID
		if (submittedForm.getFormType() == Form.FormType.INITIAL) {
//			perunHttp.createMember(1, 1);
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
		User user = perunHttp.getUserByIdentificator("TODO_GET_PRINCIPALS");
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
