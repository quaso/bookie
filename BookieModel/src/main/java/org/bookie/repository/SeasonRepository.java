package org.bookie.repository;

import java.util.Date;

import org.bookie.model.Season;
import org.springframework.data.repository.CrudRepository;

public interface SeasonRepository extends CrudRepository<Season, String> {
	public Season findByDateStartLessThanEqualAndDateEndGreaterThanEqual(Date dateStart, Date dateEnd);
}
