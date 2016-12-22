package org.bookie.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@org.hibernate.annotations.Proxy(lazy = false)
@Table(name = "`User`")
public class User extends AbstractEntity {

	@Column(name = "name", nullable = true, length = 255)
	private String name;

	@Column(name = "surname", nullable = false, length = 255)
	private String surname;

	@Column(name = "phone", nullable = true, length = 255)
	private String phone;

	@Column(name = "email", nullable = true, length = 255)
	private String email;

	@Column(name = "active", nullable = false)
	private boolean active;

	@Column(name = "verified", nullable = false)
	private boolean verified = false;

	@ManyToOne(targetEntity = Role.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "roleId", referencedColumnName = "id", nullable = false) })
	private Role role;

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

	public void setActive(final boolean value) {
		this.active = value;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setVerified(final boolean value) {
		this.verified = value;
	}

	public boolean getVerified() {
		return this.verified;
	}

	public void setRole(final Role value) {
		this.role = value;
	}

	public Role getRole() {
		return this.role;
	}

	@Override
	public String toString() {
		return "User [name=" + this.name + ", surname=" + this.surname + ", phone=" + this.phone + ", email="
				+ this.email + ", active=" + this.active + ", verified=" + this.verified + ", role=" + this.role + "]";
	}

}
