package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

	@Column
	private String moduleName;

	@Transient // possibly implement converter between formModule <-> formModuleName
	private FormModule formModule;

	@ElementCollection
	@CollectionTable(name="module_config_option")
	private List<ModuleConfigOption> configOption;

	@Column
	private int ordnum;

	@Override
	public int compareTo(AssignedFormModule o) {
		return Integer.compare(ordnum, o.ordnum);
	}
}
