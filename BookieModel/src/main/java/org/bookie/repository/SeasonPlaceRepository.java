package org.bookie.repository;

import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.bookie.model.SeasonPlace;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface SeasonPlaceRepository extends CrudRepository<SeasonPlace, String> {
	public Stream<SeasonPlace> findByPlaceEnabledTrueAndSeasonId(String seasonId);

	public void deleteByPlaceIdAndSeasonId(String placeId, String seasonId);
}
