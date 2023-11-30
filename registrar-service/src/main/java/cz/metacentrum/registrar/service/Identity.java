package cz.metacentrum.registrar.service;

import lombok.Data;

import java.util.Map;

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
	private Map<String, IdentityType> identities;
}
