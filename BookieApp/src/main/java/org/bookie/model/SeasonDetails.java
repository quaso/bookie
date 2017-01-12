package org.bookie.model;

import java.util.ArrayList;
import java.util.List;

public class SeasonDetails {
	private final Season season;
	private List<PlacesInfo> places = new ArrayList<>();

	public SeasonDetails(final Season season) {
		this(season, new ArrayList<>());
	}

	public SeasonDetails(final Season season, final List<PlacesInfo> places) {
		this.season = season;
		this.places = places;
	}

	public Season getSeason() {
		return this.season;
	}

	public List<PlacesInfo> getPlaces() {
		return this.places;
	}
}
