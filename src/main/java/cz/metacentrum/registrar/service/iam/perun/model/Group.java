package cz.metacentrum.registrar.service.iam.perun.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Group {
	private int id;
	private UUID uuid;
}
