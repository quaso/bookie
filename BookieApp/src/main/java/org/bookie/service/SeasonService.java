package org.bookie.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.bookie.exception.NotFoundException;
import org.bookie.exception.PlaceNotFoundException;
import org.bookie.exception.SeasonNotFoundException;
import org.bookie.model.Place;
import org.bookie.model.Season;
import org.bookie.model.SeasonDetails;
import org.bookie.model.SeasonPlace;
import org.bookie.repository.PlaceRepository;
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
	private PlaceRepository placeRepository;

	@Autowired
	private SeasonPlaceRepository seasonPlaceRepository;

	public Season createOrUpdateSeason(final Season season) {
		return this.seasonRepository.save(season);
	}

	public SeasonPlace assignPlaceToSeason(final String placeId, final String seasonId) throws NotFoundException {
		final Place place = this.placeRepository.findOne(placeId);
		if (place == null) {
			throw new PlaceNotFoundException(placeId);
		}
		final Season season = this.seasonRepository.findOne(seasonId);
		if (season == null) {
			throw new SeasonNotFoundException(seasonId);
		}

		final SeasonPlace sp = new SeasonPlace();
		sp.setPlace(place);
		sp.setSeason(season);
		return this.seasonPlaceRepository.save(sp);
	}

	public void unassignPlaceFromSeason(final String placeId, final String seasonId) {
		this.seasonPlaceRepository.deleteByPlaceIdAndSeasonId(placeId, seasonId);
	}

	public Season getById(final String seasonId) {
		return this.seasonRepository.findOne(seasonId);
	}

	public Season getByDate(final String organizationCode, final Date date) {
		return this.seasonRepository.findByOrganizationCodeEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
				organizationCode, date, date);
	}

	public SeasonDetails getDetailsCurrent(final String organizationCode) {
		return this.getDetailsByDate(organizationCode, new Date());
	}

	public SeasonDetails getDetailsByDate(final String organizationCode, final Date date) {
		SeasonDetails result = null;

		final Season currentSeason = this.getByDate(organizationCode, date);

		if (currentSeason != null) {
			result = new SeasonDetails(currentSeason, this.seasonPlaceRepository
					.findByPlaceEnabledTrueAndSeasonId(currentSeason.getId()).map(sp -> sp.getPlace()));
		}

		return result;
	}

}
