package org.bookie.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.bookie.exception.UserContactsBlankException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@Entity
@org.hibernate.annotations.Proxy(lazy = false)
@Table(name = "BookieUser")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends AbstractEntity {

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "name", nullable = true)
	private String name;

	@Column(name = "surname", nullable = false)
	private String surname;

	@Column(name = "phone", nullable = true)
	private String phone;

	@Column(name = "email", nullable = true)
	private String email;

	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@Column(name = "verified", nullable = false)
	private boolean verified = false;

	@Column(name = "password", nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

	@Column(name = "failedLogins", nullable = true)
	@JsonProperty(access = Access.READ_ONLY)
	private int failedLogins;

	@PrePersist
	public void prePersist() {
		this.failedLogins = 0;
		this.preUpdate();
	}

	@PreUpdate
	public void preUpdate() {
		if ((this.phone == null || this.phone.trim().length() == 0)
				&& (this.email == null || this.email.trim().length() == 0)) {
			throw new UserContactsBlankException();
		}
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public void setName(final String value) {
		this.name = value;
	}

	public String getName() {
		return this.name;
	}

	public void setSurname(final String value) {
		this.surname = value;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setPhone(final String value) {
		this.phone = value;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setEmail(final String value) {
		this.email = value;
	}

	public String getEmail() {
		return this.email;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void setVerified(final boolean value) {
		this.verified = value;
	}

	public boolean isVerified() {
		return this.verified;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public int getFailedLogins() {
		return this.failedLogins;
	}

	public void setFailedLogins(final int failedLogins) {
		this.failedLogins = failedLogins;
	}

	@Override
	public String toString() {
		return "User [username=" + this.username + ", name=" + this.name + ", surname=" + this.surname + ", phone="
				+ this.phone + ", email=" + this.email + ", enabled=" + this.enabled + ", verified=" + this.verified
				+ ", failedLogins=" + this.failedLogins + "]";
	}
}
