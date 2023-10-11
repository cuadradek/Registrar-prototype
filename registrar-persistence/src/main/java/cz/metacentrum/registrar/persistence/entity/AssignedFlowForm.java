package cz.metacentrum.registrar.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignedFlowForm {

	public enum FlowType { PRE, AUTO, REDIRECT }

	@Id
	@GeneratedValue
	private Long id;

	@Enumerated(EnumType.STRING)
	private FlowType flowType;

	@Column
	private int ordnum;

	@OneToOne
	private Form flowForm;

	@ManyToOne
	@JoinColumn(name = "main_form_id")
	@JsonIgnore
	private Form mainForm;

	@Enumerated(EnumType.STRING)
	@ElementCollection
	@CollectionTable(name = "flow_form_form_types", joinColumns = @JoinColumn(name = "assigned_flow_form_id"))
	private List<Form.FormType> ifFlowFormType = Arrays.asList(Form.FormType.INITIAL, Form.FormType.EXTENSION);

	@Enumerated(EnumType.STRING)
	@ElementCollection
	@CollectionTable(name = "main_form_form_types", joinColumns = @JoinColumn(name = "assigned_flow_form_id"))
	private List<Form.FormType> ifMainFlowType = Arrays.asList(Form.FormType.INITIAL, Form.FormType.EXTENSION);
}
