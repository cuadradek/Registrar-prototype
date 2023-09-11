package cz.metacentrum.registrar.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import java.util.List;

@Entity
public class Role {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@ElementCollection
	@CollectionTable(name = "roles_users", joinColumns = @JoinColumn(name = "role_id"))
	@Column(name="user_id")
	private List<String> assignedUsers;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAssignedUsers() {
		return assignedUsers;
	}

	public void setAssignedUsers(List<String> assignedUsers) {
		this.assignedUsers = assignedUsers;
	}
}
