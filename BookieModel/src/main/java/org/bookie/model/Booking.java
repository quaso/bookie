package org.bookie.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Booking extends AbstractEntity {
	@Temporal(TemporalType.DATE)
	private Date dateCreated;
}
