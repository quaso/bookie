package org.bookie.test.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;

import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.model.Season;
import org.bookie.model.SeasonDetails;
import org.bookie.model.SeasonDetails.PlaceGroup;
import org.bookie.repository.PlaceRepository;
import org.bookie.service.SeasonService;
import org.bookie.test.AbstractTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SeasonServiceTest extends AbstractTest {

	@Autowired
	private SeasonService seasonService;

	@Autowired
	private PlaceRepository placeRepository;

	private Organization org;

	@Before
	public void init() {
		this.org = this.createOrganization("org");
	}

	@Test
	public void currentSeasonTest() {
		final LocalDate now = LocalDate.now();
		Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date end = Date.from(
				now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1).toInstant());

		final Season season = this.createSeason(start, end, "test 1", this.org);

		final Place t1 = this.createPlace("1", "aaa", this.org);
		final Place t2 = this.createPlace("2", "aaa", this.org);
		final Place t3 = this.createPlace("3", "aaa", this.org);
		final Place s1 = this.createPlace("1", "bbb", this.org);
		final Place s2 = this.createPlace("2", "bbb", this.org);

		this.createSeasonPlace(season, t1);
		this.createSeasonPlace(season, t2);
		this.createSeasonPlace(season, t3);
		this.createSeasonPlace(season, s1);
		this.createSeasonPlace(season, s2);

		start = Date.from(now.plusYears(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		end = Date.from(now.plusYears(1).withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault())
				.minusMinutes(1).toInstant());

		final Season season2 = this.createSeason(start, end, "test 2", this.org);
		this.createSeasonPlace(season2, t2);
		this.createSeasonPlace(season2, t3);

		final SeasonDetails current = this.seasonService.getDetailsCurrent(this.org.getName());
		Assert.assertNotNull(current);
		Assert.assertNotNull(current.getSeason());
		Assert.assertEquals(season.getTimeStart(), current.getSeason().getTimeStart());
		Assert.assertEquals(2, current.getPlaceGroups().size());
		final Iterator<PlaceGroup> iterator = current.getPlaceGroups().iterator();
		final PlaceGroup placesGrp0 = iterator.next();
		final PlaceGroup placesGrp1 = iterator.next();
		if ("aaa".equals(placesGrp0.getType())) {
			Assert.assertEquals(3, placesGrp0.getPlaces().size());
			Assert.assertEquals(2, placesGrp1.getPlaces().size());
		} else {
			Assert.assertEquals(3, placesGrp1.getPlaces().size());
			Assert.assertEquals(2, placesGrp0.getPlaces().size());
		}

		final SeasonDetails seasonDetails = this.seasonService.getDetailsByDate(this.org.getName(),
				Date.from(now.plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		Assert.assertNotNull(seasonDetails);
		Assert.assertNotNull(seasonDetails.getSeason());
		Assert.assertEquals(season2.getTimeStart(), seasonDetails.getSeason().getTimeStart());
		Assert.assertEquals(1, seasonDetails.getPlaceGroups().size());
		Assert.assertEquals(2, seasonDetails.getPlaceGroups().iterator().next().getPlaces().size());

		final SeasonDetails seasonDetails2 = this.seasonService.getDetailsByDate(this.org.getName(),
				season2.getDateStart());
		Assert.assertNotNull(seasonDetails2);
		Assert.assertNotNull(seasonDetails2.getSeason());
		Assert.assertEquals(seasonDetails.getSeason().getId(), seasonDetails2.getSeason().getId());

		final SeasonDetails seasonDetails3 = this.seasonService.getDetailsByDate(this.org.getName(),
				season2.getDateEnd());
		Assert.assertNotNull(seasonDetails3);
		Assert.assertNotNull(seasonDetails3.getSeason());
		Assert.assertEquals(seasonDetails.getSeason().getId(), seasonDetails3.getSeason().getId());

		@SuppressWarnings("deprecation")
		final SeasonDetails seasonDetails4 = this.seasonService.getDetailsByDate(this.org.getName(),
				new Date(1900, 1, 1));
		Assert.assertNull(seasonDetails4);
	}

	@Test
	public void currentSeasonEmptyTest() {
		final SeasonDetails current = this.seasonService.getDetailsCurrent(this.org.getName());
		Assert.assertNull(current);
	}

	@Test
	public void getOldSeasonTest() {
		final LocalDate now = LocalDate.now();
		final Date start = Date
				.from(now.minusMonths(3).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				now.minusMonths(1).withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1)
						.toInstant());

		this.createSeason(start, end, "test 1", this.org);

		final SeasonDetails seasonDetails = this.seasonService.getDetailsByDate(this.org.getName(),
				Date.from(LocalDateTime.now().minusMonths(2).atZone(ZoneId.systemDefault()).toInstant()));
		Assert.assertNotNull(seasonDetails);
	}

	@Test
	public void disabledPlaceTest() {
		final LocalDate now = LocalDate.now();
		final Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1).toInstant());

		final Season season = this.createSeason(start, end, "test 1", this.org);

		final Place t1 = this.createPlace("1", "aaa", this.org);
		final Place t2 = new Place();
		t2.setName("2");
		t2.setType("aaa");
		t2.setOrganization(this.org);
		// disabled place
		t2.setEnabled(false);
		this.placeRepository.save(t2);
		final Place t3 = this.createPlace("3", "aaa", this.org);

		this.createSeasonPlace(season, t1);
		this.createSeasonPlace(season, t2);
		this.createSeasonPlace(season, t3);

		final SeasonDetails current = this.seasonService.getDetailsCurrent(this.org.getName());
		Assert.assertEquals(1, current.getPlaceGroups().size());
		Assert.assertEquals(2, current.getPlaceGroups().iterator().next().getPlaces().size());
	}
}
