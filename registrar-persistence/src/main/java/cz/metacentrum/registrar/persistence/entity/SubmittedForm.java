package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedForm {

	@Id
	@GeneratedValue
	@Nullable
	private Long id;

	@Column
	private Integer stepOrder;

	@NonNull
	@ManyToOne
	private Submission submission;

	@NonNull
	@ManyToOne
	@JoinColumn(name = "form_id")
	private Form form;

	@Enumerated(EnumType.STRING)
	private Form.FormType formType;

	@Enumerated(EnumType.STRING)
	private Form.FormState formState;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "submitted_form_id")
	private List<FormItemData> formData;

	@Column
	@Nullable
	private AssignedFlowForm.FlowType flowType;
}
