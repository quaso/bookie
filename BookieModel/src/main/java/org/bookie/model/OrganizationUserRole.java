package org.bookie.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "OrganizationUserRole")
public class OrganizationUserRole extends AbstractEntity {

	@ManyToOne(targetEntity = Organization.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({
			@JoinColumn(name = "OrganizationId", referencedColumnName = "id", nullable = false, updatable = false) })
	private Organization organization;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({
			@JoinColumn(name = "UserId", referencedColumnName = "id", nullable = false, updatable = false) })
	private User user;

	@ManyToOne(targetEntity = Role.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({
			@JoinColumn(name = "RoleId", referencedColumnName = "id", nullable = false, updatable = false) })
	private Role role;

	public void setValues(final Organization organization, final User user, final Role role) {
		this.organization = organization;
		this.user = user;
		this.role = role;
	}

	public Organization getOrganization() {
		return this.organization;
	}

	public User getUser() {
		return this.user;
	}

	public Role getRole() {
		return this.role;
	}

	@Override
	public String toString() {
		return "OrganizationUserRole [organization=" + this.organization.getName() + ", user=" + this.user.getUsername()
				+ ", role=" + this.role.getName() + "]";
	}

}
