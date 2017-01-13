package org.bookie.model;

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
@Table(name = "SeasonPlace", uniqueConstraints = @UniqueConstraint(columnNames = { "SeasonId", "PlaceId" }))
public class SeasonPlace extends AbstractEntity {

	@ManyToOne(targetEntity = Season.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "SeasonId", referencedColumnName = "id", nullable = false) })
	private Season season;

	@ManyToOne(targetEntity = Place.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "PlaceId", referencedColumnName = "id", nullable = false) })
	private Place place;

	public void setSeason(final Season value) {
		this.season = value;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setPlace(final Place value) {
		this.place = value;
	}

	public Place getPlace() {
		return this.place;
	}

	@Override
	public String toString() {
		return "SeasonPlace [season=" + this.season + ", place=" + this.place + "]";
	}

}
