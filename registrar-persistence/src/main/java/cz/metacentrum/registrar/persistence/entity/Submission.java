package cz.metacentrum.registrar.persistence.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Submission {

	@Id
	@GeneratedValue
	@Nullable
	private Long id;

	@NonNull
	@ManyToOne
//	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "form_id")
	private Form form;

	@Enumerated(EnumType.STRING)
	private Form.FormType formType;

	@Enumerated(EnumType.STRING)
	private Form.FormState formState;

	@Column
	private String extSourceName;

	@Column
	private String extSourceType;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<FormItemData> formData;

	@Column
	private int extSourceLoa;

	@Column
	private String submittedBy;

	@Column
	private LocalDateTime timestamp;

	public Submission() {
	}

	public Submission(@Nullable Long id, @NonNull Form form, Form.FormType formType, Form.FormState formState, String extSourceName, String extSourceType, List<FormItemData> formData, int extSourceLoa, String submittedBy, LocalDateTime timestamp) {
		this.id = id;
		this.form = form;
		this.formType = formType;
		this.formState = formState;
		this.extSourceName = extSourceName;
		this.extSourceType = extSourceType;
		this.formData = formData;
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

	@NonNull
	public Form getForm() {
		return form;
	}

	public void setForm(@NonNull Form form) {
		this.form = form;
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

	public List<FormItemData> getFormData() {
		return formData;
	}

	public void setFormData(List<FormItemData> formData) {
		this.formData = formData;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Submission that = (Submission) o;
		return extSourceLoa == that.extSourceLoa && Objects.equals(form, that.form) && formType == that.formType
				&& formState == that.formState && Objects.equals(extSourceName, that.extSourceName)
				&& Objects.equals(extSourceType, that.extSourceType) && Objects.equals(submittedBy, that.submittedBy)
				&& Objects.equals(timestamp, that.timestamp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(form, formType, formState, extSourceName, extSourceType, extSourceLoa, submittedBy, timestamp);
	}
}
