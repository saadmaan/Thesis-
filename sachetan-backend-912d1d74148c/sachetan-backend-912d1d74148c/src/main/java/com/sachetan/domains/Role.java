package com.sachetan.domains;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SequenceGenerator(name = "sequence_role_id", sequenceName = "SEQ_ROLE_ID", allocationSize = 500)

@Entity
public class Role implements GrantedAuthority {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7169843323823440283L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_role_id")
	private Long id;

	@NotEmpty
	private String name;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
	private Set<User> users = new HashSet<User>();

	protected Role() {

	}

	public Role(String role) {
		this.name = role;
	}

	@Override
	public String getAuthority() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
	
	@Override
	public String toString(){
		return getName();
	}

}
