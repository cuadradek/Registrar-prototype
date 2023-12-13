package cz.metacentrum.registrar.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

	@ManyToOne
	@JoinColumn(name = "flow_form_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Form flowForm;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_form_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Form mainForm;

	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@CollectionTable(name = "flow_form_form_types")
	@JoinColumn(name = "assigned_flow_form_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Form.FormType> ifFlowFormType = Arrays.asList(Form.FormType.INITIAL, Form.FormType.EXTENSION);

	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@CollectionTable(name = "main_form_form_types")
	@JoinColumn(name = "assigned_flow_form_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Form.FormType> ifMainFlowType = Arrays.asList(Form.FormType.INITIAL, Form.FormType.EXTENSION);
}
