package org.bookie.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SeasonDetails {
	private final Season season;
	private final Map<String, PlaceGroup> placeGroups;

	public SeasonDetails(final Season season, final Stream<Place> placesStream) {
		this.season = season;

		this.placeGroups = new HashMap<>();
		placesStream.forEach(p -> {
			this.placeGroups.computeIfAbsent(p.getType(), k -> new PlaceGroup(k)).places.add(p);
		});
	}

	public Season getSeason() {
		return this.season;
	}

	public Collection<PlaceGroup> getPlaceGroups() {
		return this.placeGroups.values();
	}

	public class PlaceGroup {
		final String type;
		final List<Place> places;

		PlaceGroup(final String type) {
			this.type = type;
			this.places = new ArrayList<>();
		}

		public String getType() {
			return this.type;
		}

		public List<Place> getPlaces() {
			return this.places;
		}

	}
}
