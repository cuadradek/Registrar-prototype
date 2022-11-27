package cz.metacentrum.registrar.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class SubmittedForm {

	@Id
	@GeneratedValue
	private Long id;

	@Enumerated(EnumType.STRING)
	private Form.FormType formType;

	@Enumerated(EnumType.STRING)
	private Form.FormState formState;

	@Column
	private String extSourceName;

	@Column
	private String extSourceType;

	@Column
	private int extSourceLoa;

	@Column
	private String submittedBy;

	@Column
	private LocalDateTime timestamp;

	public SubmittedForm() {
	}

	public SubmittedForm(Long id, Form.FormType formType, Form.FormState formState, String extSourceName, String extSourceType, int extSourceLoa, String submittedBy, LocalDateTime timestamp) {
		this.id = id;
		this.formType = formType;
		this.formState = formState;
		this.extSourceName = extSourceName;
		this.extSourceType = extSourceType;
		this.extSourceLoa = extSourceLoa;
		this.submittedBy = submittedBy;
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Form.FormType getFormType() {
		return formType;
	}

	public void setFormType(Form.FormType formType) {
		this.formType = formType;
	}

	public Form.FormState getFormState() {
		return formState;
	}

	public void setFormState(Form.FormState formState) {
		this.formState = formState;
	}

	public String getExtSourceName() {
		return extSourceName;
	}

	public void setExtSourceName(String extSourceName) {
		this.extSourceName = extSourceName;
	}

	public String getExtSourceType() {
		return extSourceType;
	}

	public void setExtSourceType(String extSourceType) {
		this.extSourceType = extSourceType;
	}

	public int getExtSourceLoa() {
		return extSourceLoa;
	}

	public void setExtSourceLoa(int extSourceLoa) {
		this.extSourceLoa = extSourceLoa;
	}

	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
