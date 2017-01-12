package org.bookie.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.bookie.model.PlacesInfo;
import org.bookie.model.SeasonPlace;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface SeasonPlaceRepository extends CrudRepository<SeasonPlace, String> {

	@Query(value = "select new org.bookie.model.PlacesInfo(sp.place.type, count(sp)) from SeasonPlace sp where sp.season.id=?1 group by sp.place.type")
	public List<PlacesInfo> findPlacesCountForSeason(String seasonId);

	public Long countBySeasonIdEqualsAndPlaceTypeEquals(String seasonId, String placeType);
}
