package com.sachetan.domains;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.validator.constraints.NotEmpty;

@SequenceGenerator(name = "sequence_incident_type_id", sequenceName = "SEQ_INCIDENT_TYPE_ID", allocationSize = 500)

@Entity
public class IncidentType {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_incident_type_id")
	private Long id;

	@OneToMany
	private Set<Report> reports = new HashSet<>();

	@NotEmpty
	@Column(unique = true)
	private String name;

	private String description;

	IncidentType() {

	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
