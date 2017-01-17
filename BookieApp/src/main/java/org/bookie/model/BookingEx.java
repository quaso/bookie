package org.bookie.model;

public class BookingEx {
	private BookingPattern pattern;
	private String type;
	private String ownerId;
	private String placeId;
	private String note;

	public BookingPattern getPattern() {
		return this.pattern;
	}

	public void setPattern(final BookingPattern pattern) {
		this.pattern = pattern;
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
