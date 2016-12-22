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
@Table(name = "SeasonPlace")
public class SeasonPlace extends AbstractEntity {

	@ManyToOne(targetEntity = Season.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "Seasonid", referencedColumnName = "id", nullable = false) })
	private Season season;

	@ManyToOne(targetEntity = Place.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "Placeid", referencedColumnName = "id", nullable = false) })
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
		final StringBuffer sb = new StringBuffer();
		sb.append("SeasonPlace[ ");
		sb.append("Id=").append(this.getId()).append(" ");
		if (this.getSeason() != null)
			sb.append("Season.Persist_ID=").append(this.getSeason().toString()).append(" ");
		else
			sb.append("Season=null ");
		if (this.getPlace() != null)
			sb.append("Place.Persist_ID=").append(this.getPlace().toString()).append(" ");
		else
			sb.append("Place=null ");
		sb.append("]");
		return sb.toString();
	}

}
