package cz.metacentrum.registrar.service.idm.perun;

import lombok.Data;

@Data
public class Member {
	private int id;
	private int userId;
	private int voId;
	private MemberStatus status;
}
