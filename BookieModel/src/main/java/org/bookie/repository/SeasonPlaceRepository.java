package org.bookie.repository;

import org.bookie.model.SeasonPlace;
import org.springframework.data.repository.CrudRepository;

public interface SeasonPlaceRepository extends CrudRepository<SeasonPlace, String> {
	public Long countBySeasonIdEqualsAndPlaceTypeEquals(String seasonId, String placeType);
}
