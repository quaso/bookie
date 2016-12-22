package org.bookie.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "Place")
public class Place extends AbstractEntity {

	@Column(name = "name", nullable = false, length = 255)
	private String name;

	@Column(name = "type", nullable = false, length = 10)
	private String type;

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

	@Override
	public String toString() {
		return "Place [name=" + this.name + ", type=" + this.type + "]";
	}

}
