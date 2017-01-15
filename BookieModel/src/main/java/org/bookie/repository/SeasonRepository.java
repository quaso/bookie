package org.bookie.repository;

import java.util.Date;

import javax.transaction.Transactional;

import org.bookie.model.Season;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface SeasonRepository extends CrudRepository<Season, String> {
	public Season findByOrganizationCodeEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
			String organizationCode, Date dateStart, Date dateEnd);
}
