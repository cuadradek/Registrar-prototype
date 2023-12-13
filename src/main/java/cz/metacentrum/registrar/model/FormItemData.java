package cz.metacentrum.registrar.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
	@OnDelete(action = OnDeleteAction.CASCADE)
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
