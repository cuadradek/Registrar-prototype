package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormItemData {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "form_item_id")
	private FormItem formItem;

	@Column(name = "item_value")
	private String value;

	@Column
	private String identityPrefilledValue;

	@Column
	private String iamPrefilledValue;

	@Column
	private Integer assuranceLevel;
}
