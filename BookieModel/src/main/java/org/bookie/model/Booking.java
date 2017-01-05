package org.bookie.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Proxy(lazy = false)
@Table(name = "Booking")
public class Booking extends AbstractEntity implements OwnerTimeSlot {

	@Column(name = "timeStart", nullable = false)
	private Date timeStart;

	@Column(name = "timeEnd", nullable = false)
	private Date timeEnd;

	@Column(name = "type", nullable = false, length = 255)
	private String type;

	@Column(name = "note", nullable = true, length = 255)
	private String note;

	@CreatedDate
	@Column(name = "createdAt", nullable = false)
	private Date createdAt;

	@ManyToOne(targetEntity = BookingPattern.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "bookingPatternId", referencedColumnName = "id") })
	private BookingPattern bookingPattern;

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

	@ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.LOCK })
	@JoinColumns({ @JoinColumn(name = "owner", referencedColumnName = "id", nullable = false) })
	private User owner;
	//
	//	@Column(name = "ownerId", nullable = false)
	//	private String ownerId;

	@Override
	public Date getTimeStart() {
		return this.timeStart;
	}

	public void setTimeStart(final Date timeStart) {
		this.timeStart = timeStart;
	}

	@Override
	public Date getTimeEnd() {
		return this.timeEnd;
	}

	public void setTimeEnd(final Date timesEnd) {
		this.timeEnd = timesEnd;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}

	public BookingPattern getBookingPattern() {
		return this.bookingPattern;
	}

	public void setBookingPattern(final BookingPattern bookingPattern) {
		this.bookingPattern = bookingPattern;
	}

	public User getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(final User createdBy) {
		this.createdBy = createdBy;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(final Season season) {
		this.season = season;
	}

	@Override
	public Place getPlace() {
		return this.place;
	}

	public void setPlace(final Place place) {
		this.place = place;
	}

	@Override
	public User getOwner() {
		return this.owner;
	}

	@Override
	public void setOwner(final User owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Booking [");
		if (this.timeStart != null) {
			builder.append("timeStart=");
			builder.append(this.timeStart);
			builder.append(", ");
		}
		if (this.timeEnd != null) {
			builder.append("timeEnd=");
			builder.append(this.timeEnd);
			builder.append(", ");
		}
		if (this.type != null) {
			builder.append("type=");
			builder.append(this.type);
			builder.append(", ");
		}
		if (this.note != null) {
			builder.append("note=");
			builder.append(this.note);
			builder.append(", ");
		}
		if (this.createdAt != null) {
			builder.append("createdAt=");
			builder.append(this.createdAt);
			builder.append(", ");
		}
		if (this.bookingPattern != null) {
			builder.append("bookingPattern=");
			builder.append(this.bookingPattern);
			builder.append(", ");
		}
		if (this.createdBy != null) {
			builder.append("createdBy=");
			builder.append(this.createdBy);
			builder.append(", ");
		}
		if (this.season != null) {
			builder.append("season=");
			builder.append(this.season);
			builder.append(", ");
		}
		if (this.place != null) {
			builder.append("place=");
			builder.append(this.place);
			builder.append(", ");
		}
		if (this.owner != null) {
			builder.append("owner=");
			builder.append(this.owner);
		}
		builder.append("]");
		return builder.toString();
	}
}
