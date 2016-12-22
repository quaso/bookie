package org.bookie.repository;

import org.bookie.model.SeasonPlace;
import org.springframework.data.repository.CrudRepository;

public interface SeasonPlaceRepository extends CrudRepository<SeasonPlace, String> {
	// @Query("SELECT COUNT(sp) FROM SeasonPlace sp WHERE sp.season.id=seasonId
	// AND sp.place.type:=placeType")
	// public Integer count(@Param("seasonId") String seasonId,
	// @Param("placeType") String placeType);

	public Long countBySeasonIdEqualsAndPlaceTypeEquals(String seasonId, String placeType);
}
