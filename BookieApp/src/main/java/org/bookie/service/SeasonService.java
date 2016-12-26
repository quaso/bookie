package org.bookie.service;

import java.util.Date;

import org.bookie.model.Season;
import org.bookie.repository.SeasonPlaceRepository;
import org.bookie.repository.SeasonRepository;
import org.bookie.service.model.PlacesInfo;
import org.bookie.service.model.SeasonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeasonService {

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private SeasonPlaceRepository seasonPlaceRepository;

	public Season createSeason(final Season season) {
		this.seasonRepository.save(season);
		return season;
	}

	public SeasonDetails getCurrent() {
		return this.getByDate(new Date());
	}

	public SeasonDetails getByDate(final Date date) {
		SeasonDetails result = null;

		final Season currentSeason = this.seasonRepository.findByDateStartLessThanEqualAndDateEndGreaterThanEqual(date,
				date);

		if (currentSeason != null) {
			result = new SeasonDetails(currentSeason);
			final String[] placeTypes = currentSeason.getTypes().split(",");
			for (final String placeType : placeTypes) {
				final PlacesInfo pi = new PlacesInfo();
				pi.setPlaceType(placeType);
				// Integer count =
				// this.seasonPlaceRepository.count(currentSeason.getId(),
				// placeType);
				final long count = this.seasonPlaceRepository
						.countBySeasonIdEqualsAndPlaceTypeEquals(currentSeason.getId(), placeType);

				pi.setPlaceCount((int) count);
				result.getPlaces().add(pi);
			}
		}

		return result;
	}

}
