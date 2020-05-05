package com.sachetan.domains;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.sachetan.util.MapPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@SequenceGenerator(name = "sequence_report_id", sequenceName = "SEQ_REPORT_ID", allocationSize = 500)

@Entity
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_report_id")
	private Long id;

	@JsonProperty(access = Access.READ_ONLY)
	@ManyToOne
	private IncidentType incidentType;

	@JsonIgnore
	@Transient
	private Long incidentTypeId;

	@JsonIgnore
	@ManyToOne
	private User user;

	@JsonIgnore
	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point location;

	private String description;
	private Integer severity;

	private Date createdAt;
	private Date updatedAt;

	Report() {

	}
	
	public Report(User user){
		this.user = user;
	}

	@JsonProperty(value = "location", access = Access.READ_ONLY)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public MapPoint getLocation() {
		if (location == null)
			return null;
		return new MapPoint(location.getX(), location.getY());
	}

	@JsonProperty(value = "location", access = Access.WRITE_ONLY)
	public void setLocation(MapPoint point) {
		WKTReader reader = new WKTReader();
		try {
			location = (Point) reader.read("POINT (" + point.getLon() + " " + point.getLat() + ")");
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonProperty(value = "incidentTypeId", access = Access.WRITE_ONLY)
	public void setIncidentTypeId(Long incidentTypeId) {
		this.incidentTypeId = incidentTypeId;
	}

	public Long getIncidentTypeId() {
		return incidentTypeId;
	}

	public IncidentType getIncidentType() {
		return incidentType;
	}

	public void setIncidentType(IncidentType incidentType) {
		this.incidentType = incidentType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSeverity() {
		return severity;
	}

	public void setSeverity(Integer severity) {
		this.severity = severity;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	@PrePersist
	protected void onCreate() {
		createdAt = new Date();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = new Date();
	}
}
