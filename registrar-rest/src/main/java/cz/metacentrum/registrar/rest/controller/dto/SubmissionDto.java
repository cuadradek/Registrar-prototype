package cz.metacentrum.registrar.rest.controller.dto;

import cz.metacentrum.registrar.persistence.entity.Form;
import cz.metacentrum.registrar.persistence.entity.FormItemData;
import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class SubmissionDto {

	@Nullable
	private Long id;

	@NotNull
	private Long formId;

	@Nullable
	private Form.FormType formType;

	@Nullable
	private Form.FormState formState;

	@Nullable
	private String extSourceName;

	@Nullable
	private String extSourceType;

	@NotNull
	private List<FormItemData> formData;

	private int extSourceLoa;

	@Nullable
	private String submittedBy;

	@Nullable
	private LocalDateTime timestamp;

	public SubmissionDto() {
	}

	@Nullable
	public Long getId() {
		return id;
	}

	public void setId(@Nullable Long id) {
		this.id = id;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	@Nullable
	public Form.FormType getFormType() {
		return formType;
	}

	public void setFormType(@Nullable Form.FormType formType) {
		this.formType = formType;
	}

	@Nullable
	public Form.FormState getFormState() {
		return formState;
	}

	public void setFormState(@Nullable Form.FormState formState) {
		this.formState = formState;
	}

	@Nullable
	public String getExtSourceName() {
		return extSourceName;
	}

	public void setExtSourceName(@Nullable String extSourceName) {
		this.extSourceName = extSourceName;
	}

	@Nullable
	public String getExtSourceType() {
		return extSourceType;
	}

	public void setExtSourceType(@Nullable String extSourceType) {
		this.extSourceType = extSourceType;
	}

	public List<FormItemData> getFormData() {
		return formData;
	}

	public void setFormData(List<FormItemData> formData) {
		this.formData = formData;
	}

	public int getExtSourceLoa() {
		return extSourceLoa;
	}

	public void setExtSourceLoa(int extSourceLoa) {
		this.extSourceLoa = extSourceLoa;
	}

	@Nullable
	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(@Nullable String submittedBy) {
		this.submittedBy = submittedBy;
	}

	@Nullable
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(@Nullable LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
