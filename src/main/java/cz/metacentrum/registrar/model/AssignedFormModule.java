package cz.metacentrum.registrar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.metacentrum.registrar.service.iam.FormModule;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Map;

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

	@ManyToOne
	@JoinColumn(name = "form_id")
	@JsonIgnore
	private Form form;

	@Transient
	@JsonIgnore
	private FormModule formModule;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="module_config_options")
	@Fetch(FetchMode.SUBSELECT)
	@MapKeyColumn(name = "config_option_name")
	@Column(name = "config_option_value")
	private Map<String, String> configOptions;

	@Column
	private int ordnum;

	@Override
	public int compareTo(AssignedFormModule o) {
		return Integer.compare(ordnum, o.ordnum);
	}
}
