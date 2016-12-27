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

	public Season getById(final String seasonId) {
		return this.seasonRepository.findOne(seasonId);
	}

	public Season getByDate(final Date date) {
		return this.seasonRepository.findByDateStartLessThanEqualAndDateEndGreaterThanEqual(date, date);
	}

	public SeasonDetails getDetailsCurrent() {
		return this.getDetailsByDate(new Date());
	}

	public SeasonDetails getDetailsByDate(final Date date) {
		SeasonDetails result = null;

		final Season currentSeason = this.getByDate(date);

		if (currentSeason != null) {
			result = new SeasonDetails(currentSeason);
			final String[] placeTypes = currentSeason.getTypes().split(",");
			for (final String placeType : placeTypes) {
				final PlacesInfo pi = new PlacesInfo();
				pi.setPlaceType(placeType);
				final long count = this.seasonPlaceRepository
						.countBySeasonIdEqualsAndPlaceTypeEquals(currentSeason.getId(), placeType);
				pi.setPlaceCount((int) count);
				result.getPlaces().add(pi);
			}
		}

		return result;
	}

}
