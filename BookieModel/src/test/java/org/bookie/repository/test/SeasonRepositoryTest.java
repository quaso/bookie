package org.bookie.repository.test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.transaction.Transactional;

import org.bookie.model.Organization;
import org.bookie.model.Season;
import org.bookie.repository.OrganizationRepository;
import org.bookie.repository.SeasonRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainTestConfiguration.class)
@ActiveProfiles("test")
@Transactional
public class SeasonRepositoryTest {

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Test
	public void testFind() {
		final Organization org1 = this.createOrganization("org1");
		final Organization org2 = this.createOrganization("org2");
		this.createOrganization("org3");

		final Date start = Date.from(LocalDate.of(2016, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(LocalDate.of(2016, 3, 31).atStartOfDay(ZoneId.systemDefault()).toInstant());

		final Season season11 = this.createSeason(start, end, "season11", org1);
		final Season season12 = this.createSeason(start, end, "season12", org2);
		final Season season22 = this.createSeason(
				Date.from(LocalDate.of(2016, 4, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
				Date.from(LocalDate.of(2016, 4, 30).atStartOfDay(ZoneId.systemDefault()).toInstant()), "season22",
				org2);

		Date day = Date.from(LocalDate.of(2016, 3, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Season season = this.seasonRepository
				.findByOrganizationNameEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(org2.getName(), day,
						day);
		Assert.assertNotNull(season);
		Assert.assertEquals(season12.getId(), season.getId());

		season = this.seasonRepository.findByOrganizationNameEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
				org1.getName(), day, day);
		Assert.assertNotNull(season);
		Assert.assertEquals(season11.getId(), season.getId());

		day = Date.from(LocalDate.of(2016, 4, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		season = this.seasonRepository.findByOrganizationNameEqualsAndDateStartLessThanEqualAndDateEndGreaterThanEqual(
				org2.getName(), day, day);
		Assert.assertNotNull(season);
		Assert.assertEquals(season22.getId(), season.getId());

	}

	private Organization createOrganization(final String name) {
		final Organization org = new Organization();
		org.setName(name);
		this.organizationRepository.save(org);
		return org;
	}

	private Season createSeason(final Date start, final Date end, final String name, final Organization organization) {
		final Season season = new Season();
		season.setDateStart(start);
		season.setDateEnd(end);
		season.setTimeStart(7 * 60);
		season.setTimeEnd(22 * 60);
		season.setName(name);
		season.setTypes("aaa,bbb");
		season.setOrganization(organization);
		this.seasonRepository.save(season);
		return season;
	}
}
