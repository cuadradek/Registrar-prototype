package cz.metacentrum.registrar.service.idm.perun.modules;

import cz.metacentrum.registrar.persistence.entity.Submission;
import cz.metacentrum.registrar.service.idm.perun.PerunHttp;
import org.springframework.stereotype.Component;

@Component
public class AddToVo extends PerunFormModule {

	public AddToVo(PerunHttp perunHttp) {
		super(perunHttp);
	}

	@Override
	public Submission beforeApprove(Submission submission) {
		return submission;
	}

	@Override
	public Submission onApprove(Submission submission) {
		// TODO: getCurrentUser, getVoByUUID
		perunHttp.createMember(1, 1);
		return submission;
	}

	@Override
	public Submission onReject(Submission submission) {
		return submission;
	}
}
