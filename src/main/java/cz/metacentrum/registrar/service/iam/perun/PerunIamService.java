package cz.metacentrum.registrar.service.iam.perun;

import cz.metacentrum.perun.openapi.model.Group;
import cz.metacentrum.perun.openapi.model.User;
import cz.metacentrum.perun.openapi.model.Vo;
import cz.metacentrum.registrar.service.iam.IamService;
import cz.metacentrum.registrar.model.Identity;
import cz.metacentrum.registrar.service.iam.perun.client.PerunEnhancedRPC;
import cz.metacentrum.registrar.service.iam.perun.client.PerunHttp;
import cz.metacentrum.registrar.service.iam.perun.client.PerunRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PerunIamService implements IamService {

	private final PerunHttp perunHttp;
	private final PerunEnhancedRPC perunRPC;
//	private final PerunRPC perunRPC;

	public PerunIamService(PerunHttp perunHttp, PerunEnhancedRPC perunRPC) {
		this.perunHttp = perunHttp;
		this.perunRPC = perunRPC;
	}

	@Value("${perun.primary-ext-source}")
	private String primaryExtSource;

	@Override
	public List<UUID> getUserGroups(String userIdentifier) {
		Optional<User> user = perunRPC.getUserByIdentifier(userIdentifier);
		if (user.isEmpty()) return new ArrayList<>();

		List<Vo> vos = perunRPC.getUsersManager().getVosWhereUserIsMember(user.get().getId());
		return vos.stream()
				.map(vo -> perunRPC.getMembersManager().getMemberByUser(user.get().getId(), vo.getId()))
				.flatMap(member -> perunRPC.getGroupsManager().getMemberGroups(member.getId()).stream())
				.map(Group::getUuid)
				.collect(Collectors.toList());
	}

	@Override
	public boolean canCreateForm(String userIdentifier) {
		Optional<User> user = perunRPC.getUserByIdentifier(userIdentifier);
		if (user.isEmpty()) return false;
		var roles = perunRPC.getAuthzResolver().getUserRoles(user.get().getId());
		return roles.containsKey("VOADMIN") || roles.containsKey("PERUNADMIN") || roles.containsKey("GROUPADMIN");
	}

	@Override
	public boolean isObjectRightHolder(String userIdentifier, UUID iamObject) {
		//todo: get the object by UUID-> if it is VO, then check for VO ADMIN
		//todo: else if group, check for GROUPADMIN
		//todo: need to implement method in perun to get object by UUID
		return getUserRoles(userIdentifier).contains("PERUNADMIN");
	}

	@Override
	public List<String> getUserRoles(String userIdentifier) {
		Optional<User> user = perunRPC.getUserByIdentifier(userIdentifier);
		if (user.isEmpty()) return new ArrayList<>();

		return perunRPC.getAuthzResolver().getUserRoles(user.get().getId()).keySet().stream().toList();
	}

	@Override
	public String getUserAttributeValue(String userIdentifier, String attributeName) {
		Optional<User> user = perunRPC.getUserByIdentifier(userIdentifier);
		if (user.isEmpty()) return null;

		try {
			var value = perunRPC.getAttributesManager().getAttribute(attributeName, null, null, user.get().getId(), null, null,
					null, null, null, null, null).getValue();
			return value == null ? null : value.toString();
		} catch (PerunRuntimeException ex) {
			if (ex.getName().equals("AttributeNotExistsException")) {
				return null;
			} else {
				throw ex;
			}
		}
	}

	@Override
	public boolean userExists(String userIdentifier) {
		return perunRPC.getUserByIdentifier(userIdentifier).isPresent();
	}

	@Override
	public List<Identity> getSimilarUsers(Map<String, Object> claims) {
		return List.of();
	}
}
