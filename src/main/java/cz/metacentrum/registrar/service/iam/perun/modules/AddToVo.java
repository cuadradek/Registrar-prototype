package cz.metacentrum.registrar.service.iam.perun.modules;

import cz.metacentrum.perun.openapi.model.Candidate;
import cz.metacentrum.perun.openapi.model.ExtSource;
import cz.metacentrum.perun.openapi.model.InputCreateMemberForCandidate;
import cz.metacentrum.perun.openapi.model.InputCreateMemberForUser;
import cz.metacentrum.perun.openapi.model.Member;
import cz.metacentrum.perun.openapi.model.User;
import cz.metacentrum.perun.openapi.model.UserExtSource;
import cz.metacentrum.registrar.model.Form;
import cz.metacentrum.registrar.model.SubmittedForm;
import cz.metacentrum.registrar.security.PrincipalService;
import cz.metacentrum.registrar.security.RegistrarPrincipal;
import cz.metacentrum.registrar.service.SubmissionService;
import cz.metacentrum.registrar.service.iam.perun.client.PerunEnhancedRPC;
import cz.metacentrum.registrar.service.iam.perun.client.PerunRuntimeException;
import cz.metacentrum.registrar.service.iam.perun.client.PerunHttp;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AddToVo extends PerunFormModule {

	private static final String VO = "VO";
	private final PrincipalService principalService;
	private final SubmissionService submissionService;

	public AddToVo(PerunHttp perunHttp, PerunEnhancedRPC perunRPC, PrincipalService principalService, SubmissionService submissionService) {
		super(perunHttp, perunRPC);
		this.principalService = principalService;
		this.submissionService = submissionService;
	}

	@Override
	public List<String> getConfigOptions() {
		return List.of(VO);
	}

	@Override
	public void beforeApprove(SubmittedForm submittedForm) {
	}

	@Override
	public void onApprove(SubmittedForm submittedForm, Map<String, String> configOptions) {
		Optional<User> user = perunRPC.getUserByIdentifier(submittedForm.getSubmission().getSubmitterId());

		if (submittedForm.getFormType() == Form.FormType.INITIAL) {
			if (user.isEmpty()) {
				var input = new InputCreateMemberForCandidate();
				input.setVo(Integer.parseInt(configOptions.get(VO)));
				input.setCandidate(getCandidate(submittedForm));
				Member member = perunRPC.getMembersManager().createMemberForCandidate(input);
				// TODO: now that the user is created in IAM, his submission need to be marked with his new identifier
				submissionService.consolidateSubmissions(
						member.getId().toString(), // TODO: change for his actual new identifier
						submittedForm.getSubmission().getOriginalIdentityIdentifier(),
						submittedForm.getSubmission().getOriginalIdentityIssuer());
			} else {
				var input = new InputCreateMemberForUser();
				input.setUser(user.get().getId());
				input.setVo(Integer.parseInt(configOptions.get(VO)));
				perunRPC.getMembersManager().createMemberForUser(input);
			}
		} else if (submittedForm.getFormType() == Form.FormType.EXTENSION) {
			Member member = perunRPC.getMembersManager().getMemberByUser(Integer.parseInt(configOptions.get(VO)), user.get().getId());
			perunRPC.getMembersManager().extendMembership(member.getId());
		}
	}

	private Candidate getCandidate(SubmittedForm submittedForm) {
		Candidate candidate = new Candidate();
		// todo get this from identity info
		candidate.setFirstName("TODOfirst");
		candidate.setLastName("TODOsecond");
		if (submittedForm.getSubmission().getSubmitterName() != null) {
			var ues = new UserExtSource();
			ues.setLoa(submittedForm.getSubmission().getOriginalIdentityLoa());
			ues.setLogin(submittedForm.getSubmission().getSubmitterName());
			var es = new ExtSource();
//		es.setName(principalService.getPrincipal().getIssuer().toString());
			es.setName("INTERNAL");
			es.setType("cz.metacentrum.perun.core.impl.ExtSourceInternal");
//		es.setType("cz.metacentrum.perun.core.impl.ExtSourceIdp");
			ues.setExtSource(es);
			candidate.setUserExtSource(ues);
		}
		return candidate;
	}

	@Override
	public void onReject(SubmittedForm submittedForm) {
	}

	@Override
	public List<SubmittedForm> onLoad(SubmittedForm submittedForm, Map<String, String> configOptions) {
		Optional<User> user = perunRPC.getUserByIdentifier(principalService.getPrincipal().getId());
		if (user.isEmpty()) {
			submittedForm.setFormType(Form.FormType.INITIAL);
		} else {
			try {
				Member member = perunRPC.getMembersManager().getMemberByUser(Integer.parseInt(configOptions.get(VO)), user.get().getId());
				if (!perunRPC.getMembersManager().canExtendMembership(member.getId())) {
					return List.of();
				}
				submittedForm.setFormType(Form.FormType.EXTENSION);
			} catch (PerunRuntimeException ex) {
				if (ex.getName().equals("MemberNotExistsException")) {
					submittedForm.setFormType(Form.FormType.INITIAL);
				}
			}
		}
		return List.of(submittedForm);
	}

	@Override
	public boolean hasRightToAddToForm(Form form, Map<String, String> configOptions) {
		RegistrarPrincipal principal = principalService.getPrincipal();
		Optional<User> user = perunRPC.getUserByIdentifier(principal.getId());
		if (user.isEmpty()) {
			return false;
		}

		var roles = perunRPC.getAuthzResolver().getUserRoles(user.get().getId());
		if (roles.containsKey("PERUNADMIN")) {
			return true;
		}

		var voAdmin = roles.get("VOADMIN");
		if (voAdmin == null) {
			return false;
		}
		var vos = voAdmin.get("Vo");
		return vos != null && vos.contains(Integer.parseInt(configOptions.get(VO)));
	}
}
