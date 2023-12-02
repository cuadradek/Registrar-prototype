package cz.metacentrum.registrar.service.idm.perun;

import cz.metacentrum.perun.openapi.PerunRPC;
import cz.metacentrum.perun.openapi.model.Group;
import cz.metacentrum.perun.openapi.model.User;
import cz.metacentrum.perun.openapi.model.Vo;
import cz.metacentrum.registrar.service.IamService;
import cz.metacentrum.registrar.persistence.entity.Identity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	public User getUserByIdentifier(String userIdentifier) {
		return perunRPC.getUserByIdentifier(userIdentifier);
	}

	@Override
	public List<UUID> getUserGroups(String userIdentifier) {
		User user = getUserByIdentifier(userIdentifier);
		if (user == null) return new ArrayList<>();

		List<Vo> vos = perunRPC.getUsersManager().getVosWhereUserIsMember(user.getId());
		return vos.stream()
				.map(vo -> perunRPC.getMembersManager().getMemberByUser(user.getId(), vo.getId()))
				.flatMap(member -> perunRPC.getGroupsManager().getMemberGroups(member.getId()).stream())
				.map(Group::getUuid)
				.collect(Collectors.toList());
	}

	@Override
	public boolean canCreateForm(String userIdentifier) {
		User user = getUserByIdentifier(userIdentifier);
		if (user == null) return false;
		var roles = perunRPC.getAuthzResolver().getUserRoles(user.getId());
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
		User user = getUserByIdentifier(userIdentifier);
		if (user == null) return new ArrayList<>();
		return perunRPC.getAuthzResolver().getUserRoles(user.getId()).keySet().stream().toList();
	}

	@Override
	public String getUserAttributeValue(String userIdentifier, String attributeName) {
		User user = getUserByIdentifier(userIdentifier);
		if (user == null) return null;
		var value = perunRPC.getAttributesManager().getAttribute(attributeName, null, null, user.getId(), null, null,
				null, null, null, null, null).getValue();
		return value == null ? null : value.toString();
	}

	@Override
	public boolean userExists(String userIdentifier) {
		return getUserByIdentifier(userIdentifier) != null;
	}

	@Override
	public List<Identity> getSimilarUsers(Map<String, Object> claims) {
		return List.of();
	}
}
