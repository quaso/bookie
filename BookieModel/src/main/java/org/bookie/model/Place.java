package org.bookie.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "Place", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "type", "organizationId" }))
public class Place extends AbstractEntity {

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "type", nullable = false)
	private String type;

	@Column(name = "enabled", nullable = false)
	private boolean enabled = true;

	@ManyToOne(targetEntity = Organization.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "organizationId", referencedColumnName = "id", nullable = false) })
	private Organization organization;

	public void setName(final String value) {
		this.name = value;
	}

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public Organization getOrganization() {
		return this.organization;
	}

	public void setOrganization(final Organization organization) {
		this.organization = organization;
	}

	@Override
	public String toString() {
		return "Place [name=" + this.name + ", type=" + this.type + ", enabled=" + this.enabled + ", organization="
				+ this.organization.getCode() + "]";
	}

}
