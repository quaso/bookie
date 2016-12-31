package org.bookie.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Proxy;

@Entity
@Proxy(lazy = false)
@Table(name = "Season")
public class Season extends AbstractEntity {

	@Column(name = "name", nullable = false, unique = true, length = 255)
	private String name;

	@Column(name = "dateStart", nullable = false, unique = true)
	@Temporal(TemporalType.DATE)
	private Date dateStart;

	@Column(name = "dateEnd", nullable = true)
	@Temporal(TemporalType.DATE)
	private Date dateEnd;

	@Column(name = "timeStart", nullable = false)
	private int timeStart;

	@Column(name = "timeEnd", nullable = false)
	private int timeEnd;

	// @JsonIgnore
	// TODO: List<> ???
	@Column(name = "types", nullable = false, length = 255)
	private String types;

	public void setName(final String value) {
		this.name = value;
	}

	public String getName() {
		return this.name;
	}

	public Date getDateStart() {
		return this.dateStart;
	}

	public void setDateStart(final Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return this.dateEnd;
	}

	public void setDateEnd(final Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	/**
	 * Minutes in day when booking can start
	 */
	public void setTimeStart(final int value) {
		this.timeStart = value;
	}

	/**
	 * Minutes in day when booking can start
	 */
	public int getTimeStart() {
		return this.timeStart;
	}

	/**
	 * Minutes in day when booking can end
	 */
	public void setTimeEnd(final int value) {
		this.timeEnd = value;
	}

	/**
	 * Minutes in day when booking can end
	 */
	public int getTimeEnd() {
		return this.timeEnd;
	}

	public String getTypes() {
		return this.types;
	}

	public void setTypes(final String types) {
		this.types = types;
	}

	@Override
	public String toString() {
		return "Season [name=" + this.name + ", dateStart=" + this.dateStart + ", dateEnd=" + this.dateEnd
				+ ", timeStart=" + this.timeStart + ", timeEnd=" + this.timeEnd + ", types=" + this.types + "]";
	}

}
