package org.bookie.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "BookingPattern")
public class BookingPattern extends AbstractEntity {

	@Column(name = "pattern", nullable = false, length = 255)
	private String pattern;

	@Column(name = "timeStart", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date timeStart;

	@Column(name = "timeEnd", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date timeEnd;

	@Column(name = "type", nullable = false, length = 255)
	private String type;

	@Column(name = "createdAt", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date createdAt;

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "createdBy", referencedColumnName = "id", nullable = false) })
	private User createdBy;

	@ManyToOne(targetEntity = Season.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "seasonId", referencedColumnName = "id", nullable = false) })
	private Season season;

	@ManyToOne(targetEntity = Place.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "placeId", referencedColumnName = "id", nullable = false) })
	private Place place;

	public void setPattern(final String value) {
		this.pattern = value;
	}

	public String getPattern() {
		return this.pattern;
	}

	public Date getTimeStart() {
		return this.timeStart;
	}

	public void setTimeStart(final Date timeStart) {
		this.timeStart = timeStart;
	}

	public Date getTimeEnd() {
		return this.timeEnd;
	}

	public void setTimeEnd(final Date timeEnd) {
		this.timeEnd = timeEnd;
	}

	public void setType(final String value) {
		this.type = value;
	}

	public String getType() {
		return this.type;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}

	public void setCreatedBy(final User value) {
		this.createdBy = value;
	}

	public User getCreatedBy() {
		return this.createdBy;
	}

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
		return "BookingPattern [pattern=" + this.pattern + ", timeStart=" + this.timeStart + ", timeEnd=" + this.timeEnd
				+ ", type=" + this.type + ", createdAt=" + this.createdAt + ", createdBy=" + this.createdBy
				+ ", season=" + this.season + ", place=" + this.place + "]";
	}

}
