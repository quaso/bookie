package org.bookie.service;

import javax.transaction.Transactional;

import org.bookie.model.Place;
import org.bookie.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PlaceService {

	@Autowired
	private PlaceRepository placeRepository;

	public Place createOrUpdatePlace(final Place place) {
		return this.placeRepository.save(place);
	}

	//	public Season getById(final String seasonId) {
	//		return this.seasonRepository.findOne(seasonId);
	//	}
	//
	//	public Season getByDate(final String organizationName, final Date date) {
	//		return this.seasonRepository.findByOrganizationNameEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
	//				organizationName, date, date);
	//	}
	//
	//	public SeasonDetails getDetailsCurrent(final String organizationName) {
	//		return this.getDetailsByDate(organizationName, new Date());
	//	}
	//
	//	public SeasonDetails getDetailsByDate(final String organizationName, final Date date) {
	//		SeasonDetails result = null;
	//
	//		final Season currentSeason = this.getByDate(organizationName, date);
	//
	//		if (currentSeason != null) {
	//			result = new SeasonDetails(currentSeason,
	//					this.seasonPlaceRepository.findPlacesCountForSeason(currentSeason.getId()));
	//		}
	//
	//		return result;
	//	}

}
