package org.bookie.service.model;

import java.util.ArrayList;
import java.util.List;

import org.bookie.model.Season;

public class SeasonDetails {
	private Season season;
	private List<PlacesInfo> places = new ArrayList<>();

	public SeasonDetails(final Season season) {
		this.season = season;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(final Season season) {
		this.season = season;
	}

	public List<PlacesInfo> getPlaces() {
		return this.places;
	}

	public void setPlaces(final List<PlacesInfo> places) {
		this.places = places;
	}

}
