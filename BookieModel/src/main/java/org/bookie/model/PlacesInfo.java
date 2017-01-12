package org.bookie.model;

public class PlacesInfo {

	private long placeCount;
	private String placeType;

	public PlacesInfo() {
	}

	public PlacesInfo(final String placeType, final long placeCount) {
		this.placeType = placeType;
		this.placeCount = placeCount;
	}

	public long getPlaceCount() {
		return this.placeCount;
	}

	public void setPlaceCount(final long placeCount) {
		this.placeCount = placeCount;
	}

	public String getPlaceType() {
		return this.placeType;
	}

	public void setPlaceType(final String placeType) {
		this.placeType = placeType;
	}

}