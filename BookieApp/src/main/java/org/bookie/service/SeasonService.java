package org.bookie.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.bookie.model.Season;
import org.bookie.model.SeasonDetails;
import org.bookie.repository.SeasonPlaceRepository;
import org.bookie.repository.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SeasonService {

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private SeasonPlaceRepository seasonPlaceRepository;

	public Season createOrUpdateSeason(final Season season) {
		return this.seasonRepository.save(season);
	}

	public Season getById(final String seasonId) {
		return this.seasonRepository.findOne(seasonId);
	}

	public Season getByDate(final String organizationName, final Date date) {
		return this.seasonRepository.findByOrganizationNameEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
				organizationName, date, date);
	}

	public SeasonDetails getDetailsCurrent(final String organizationName) {
		return this.getDetailsByDate(organizationName, new Date());
	}

	public SeasonDetails getDetailsByDate(final String organizationName, final Date date) {
		SeasonDetails result = null;

		final Season currentSeason = this.getByDate(organizationName, date);

		if (currentSeason != null) {
			result = new SeasonDetails(currentSeason,
					this.seasonPlaceRepository.findPlacesCountForSeason(currentSeason.getId()));
		}

		return result;
	}

}
