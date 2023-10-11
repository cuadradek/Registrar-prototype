package cz.metacentrum.registrar.service.idm.perun.modules;

import cz.metacentrum.registrar.persistence.entity.SubmittedForm;
import cz.metacentrum.registrar.service.idm.perun.PerunHttp;
import org.springframework.stereotype.Component;

@Component
public class AddToVo extends PerunFormModule {

	public AddToVo(PerunHttp perunHttp) {
		super(perunHttp);
	}

	@Override
	public SubmittedForm beforeApprove(SubmittedForm submittedForm) {
		return submittedForm;
	}

	@Override
	public SubmittedForm onApprove(SubmittedForm submittedForm) {
		// TODO: getCurrentUser, getVoByUUID
//		perunHttp.createMember(1, 1);
		return submittedForm;
	}

	@Override
	public SubmittedForm onReject(SubmittedForm submittedForm) {
		return submittedForm;
	}
}
