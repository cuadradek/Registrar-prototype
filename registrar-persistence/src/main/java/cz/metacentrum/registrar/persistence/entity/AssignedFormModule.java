package cz.metacentrum.registrar.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignedFormModule implements Comparable<AssignedFormModule> {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "form_id")
	@JsonIgnore
	private Form form;

	@Column
	private String moduleName;

	@Transient // possibly implement converter between formModule <-> formModuleName
	private FormModule formModule;

	@OneToMany(mappedBy = "assignedModule")
	private List<ModuleConfigOption> configOption;

	@Column
	private int ordnum;

	@Override
	public int compareTo(AssignedFormModule o) {
		if (ordnum == o.ordnum) {
			return 0;
		}
		if (ordnum < o.ordnum) {
			return -1;
		}
		return 1;
	}
}
