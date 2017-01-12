package org.bookie.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.bookie.exception.UserContactsBlankException;

import net.minidev.json.annotate.JsonIgnore;

@Entity
@org.hibernate.annotations.Proxy(lazy = false)
@Table(name = "BookieUser")
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
	private String password;

	@Column(name = "failedLogins", nullable = true)
	private int failedLogins;

	@PrePersist
	public void prePersist() {
		this.failedLogins = 0;
		this.preUpdate();
	}

	@PreUpdate
	public void preUpdate() {
		if (StringUtils.isBlank(this.phone) && StringUtils.isBlank(this.email)) {
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

	public boolean getVerified() {
		return this.verified;
	}

	@JsonIgnore
	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public int getFailedLogins() {
		return this.failedLogins;
	}

	@JsonIgnore
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
