package org.bookie.model;

import java.util.Date;

public class BookingEx {
	private Date timeStart;
	private Date timeEnd;
	private String type;
	private String ownerId;
	private String placeId;
	private String note;

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

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(final String ownerId) {
		this.ownerId = ownerId;
	}

	public String getPlaceId() {
		return this.placeId;
	}

	public void setPlaceId(final String placeId) {
		this.placeId = placeId;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

}
