package org.bookie.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Role")
public class Role extends AbstractEntity {

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	public void setName(final String value) {
		this.name = value;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "Role [name=" + this.name + "]";
	}
}
