package org.bookie.repository;

import java.util.Date;

import org.bookie.model.Season;
import org.springframework.data.repository.CrudRepository;

public interface SeasonRepository extends CrudRepository<Season, String> {
	public Season findByOrganizationNameEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
			String organizationName, Date dateStart, Date dateEnd);
}
