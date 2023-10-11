package cz.metacentrum.registrar.rest.controller.dto;

import lombok.Data;

import java.util.UUID;

/**
 * Form without relationships - will not fetch them from DB.
 */
@Data
public class ShortFormDto {
	private Long id;
	private UUID idmObject;
	private UUID idmFormManagersGroup;
	private String name;
	private String urlSuffix;
	private String redirectUrl;
	boolean canBeResubmitted;
	boolean autoApprove;
}
