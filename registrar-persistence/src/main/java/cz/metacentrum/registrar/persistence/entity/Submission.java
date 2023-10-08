package cz.metacentrum.registrar.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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
