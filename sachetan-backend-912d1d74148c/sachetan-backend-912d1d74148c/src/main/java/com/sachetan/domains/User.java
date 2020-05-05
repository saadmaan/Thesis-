package com.sachetan.domains;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.sachetan.util.MapPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;;

@SequenceGenerator(name = "sequence_user_id", sequenceName = "SEQ_USER_ID", allocationSize = 100)

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_user_id")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(nullable = false)
	private String password;

	@JsonIgnore
	@Type(type = "org.hibernate.spatial.GeometryType")
	private Point lastKnownLocation;

	private float reliability;
	private String firstName;
	private String lastName;
	private String phone;

	@Column(nullable = false, unique = true)
	private String email;

	@JsonIgnore
	private boolean blacklisted;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id") })
	private Set<Role> roles = new HashSet<Role>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Report> reports = new HashSet<>();

	public User() {
	}

	public User(String username, String password, String email, String phone, String firstName, String lastName) {
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
	}

	public User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public User(User user) {
		this.setFirstName(user.getFirstName());
		this.setLastName(user.getLastName());
		this.setRoles(user.getRoles());
		this.setPassword(user.getPassword());
		this.setUsername(user.getUsername());
		this.setBlacklisted(user.isBlacklisted());

		MapPoint lastKnownLocation = user.getLastKnownLocation();
		if (lastKnownLocation != null) {
			this.setLastKnownLocation(lastKnownLocation);
		}

		this.setID(user.getID());
		this.setEmail(user.getEmail());
		this.setReliability(user.getReliability());
	}

	public Long getID() {
		return id;
	}

	public void setID(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public float getReliability() {
		return reliability;
	}

	public void setReliability(float reliability) {
		this.reliability = reliability;
	}

	public boolean isBlacklisted() {
		return blacklisted;
	}

	public void setBlacklisted(boolean blacklisted) {
		this.blacklisted = blacklisted;
	}

	@JsonProperty(value = "lastKnownLocation", access = Access.READ_ONLY)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public MapPoint getLastKnownLocation() {
		if (lastKnownLocation == null)
			return null;
		return new MapPoint(lastKnownLocation.getX(), lastKnownLocation.getY());
	}

	@JsonProperty(value = "lastKnownLocation", access = Access.WRITE_ONLY)
	public void setLastKnownLocation(MapPoint point) {
		WKTReader reader = new WKTReader();
		try {
			lastKnownLocation = (Point) reader.read("POINT (" + point.getLon() + " " + point.getLat() + ")");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "->" + getUsername() + "/" + getEmail() + "/" + getPassword() + "/" + getRoles().toString();
	}

	public Set<Report> getReports() {
		return Collections.unmodifiableSet(reports);
	}

}
