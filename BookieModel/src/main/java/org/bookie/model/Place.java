package org.bookie.model;

import javax.persistence.Entity;

@Entity
public class Place extends AbstractEntity {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
