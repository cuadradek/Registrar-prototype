package cz.metacentrum.registrar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.MapKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormMessage {

	public enum Event { AUTO_APPROVED, WAITING_FOR_APPROVAL, WAITING_FOR_MAIL_VERIFICATION }

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "form_id")
	@JsonIgnore
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Form form;

	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "form_message_form_types")
	@JoinColumn(name = "notification_id")
	@Fetch(FetchMode.SUBSELECT)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Form.FormType> formTypes = Arrays.asList(Form.FormType.INITIAL, Form.FormType.EXTENSION);

	@Enumerated(EnumType.STRING)
	private Event event;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="form_message_contents")
	@Fetch(FetchMode.SUBSELECT)
	@MapKeyColumn(name = "message_locale")
	@Column(name = "message_content")
	private Map<Locale, String> localizedMessages;
}
