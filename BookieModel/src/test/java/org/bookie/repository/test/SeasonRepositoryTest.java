package org.bookie.repository.test;

import java.time.LocalDate;
import java.util.Date;

import org.bookie.model.Organization;
import org.bookie.model.Season;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class SeasonRepositoryTest extends AbstractTest {

	@Test
	public void testFind() {
		final Organization org1 = this.createOrganization("org1");
		final Organization org2 = this.createOrganization("org2");
		this.createOrganization("org3");

		final Date start = this.date(LocalDate.of(2016, 1, 1));
		final Date end = this.date(LocalDate.of(2016, 3, 31));

		final Season season11 = this.createSeason(start, end, "season11", "aaa,bbb", org1);
		final Season season12 = this.createSeason(start, end, "season12", "aaa,bbb", org2);
		final Season season22 = this.createSeason(this.date(LocalDate.of(2016, 4, 1)),
				this.date(LocalDate.of(2016, 4, 30)), "season22", "aaa,bbb", org2);

		Date day = this.date(LocalDate.of(2016, 3, 1));
		Season season = this.seasonRepository
				.findByOrganizationNameEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(org2.getName(), day,
						day);
		Assert.assertNotNull(season);
		Assert.assertEquals(season12.getId(), season.getId());

		season = this.seasonRepository.findByOrganizationNameEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
				org1.getName(), day, day);
		Assert.assertNotNull(season);
		Assert.assertEquals(season11.getId(), season.getId());

		day = this.date(LocalDate.of(2016, 4, 1));
		season = this.seasonRepository.findByOrganizationNameEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
				org2.getName(), day, day);
		Assert.assertNotNull(season);
		Assert.assertEquals(season22.getId(), season.getId());
	}

	@Test()
	public void testCreateUniqueName() {
		final Organization org = this.createOrganization("org1");

		final Date start = this.date(LocalDate.of(2016, 1, 1));
		final Date end = this.date(LocalDate.of(2016, 3, 31));

		final Season season = this.createSeason(start, end, "season", "aaa,bbb", org);
		this.createSeason(start, end, season.getName() + "a", "aaa,bbb", season.getOrganization());
		this.seasonRepository.findAll();
		try {
			this.createSeason(start, end, season.getName(), "aaa,bbb", season.getOrganization());
			this.seasonRepository.findAll();
			Assert.fail("Unique constraint for name/organization failed");
		} catch (final DataIntegrityViolationException ex) {
		}
	}
}
