package cz.metacentrum.registrar.model;

import lombok.Data;

@Data
public class Identity {

	public enum IdentityType {
		IDP,
		PASSWORD,
		CERT
	}

	private String name;
	private String organization;
	private String email;
	private IdentityType type;
}
