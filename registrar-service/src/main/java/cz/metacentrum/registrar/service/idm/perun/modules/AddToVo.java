package cz.metacentrum.registrar.service.idm.perun.modules;

import cz.metacentrum.registrar.persistence.entity.Submission;

public class AddToVo extends PerunFormModule {

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
