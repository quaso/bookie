package org.bookie.test.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.transaction.Transactional;

import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.model.PlacesInfo;
import org.bookie.model.Season;
import org.bookie.model.SeasonDetails;
import org.bookie.model.SeasonPlace;
import org.bookie.repository.OrganizationRepository;
import org.bookie.repository.PlaceRepository;
import org.bookie.repository.SeasonPlaceRepository;
import org.bookie.repository.SeasonRepository;
import org.bookie.service.SeasonService;
import org.bookie.test.TestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class SeasonServiceTest {

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private SeasonPlaceRepository seasonPlaceRepository;

	@Autowired
	private SeasonService seasonService;

	@Test
	public void currentSeasonTest() {
		final LocalDate now = LocalDate.now();
		Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date end = Date.from(
				now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1).toInstant());

		final Organization org = new Organization();
		org.setName("org");
		this.organizationRepository.save(org);

		final Season season = new Season();
		season.setDateStart(start);
		season.setDateEnd(end);
		season.setTimeStart(7 * 60);
		season.setTimeEnd(22 * 60);
		season.setName("test 1");
		season.setTypes("aaa,bbb");
		season.setOrganization(org);
		this.seasonRepository.save(season);

		final Place t1 = this.createPlace("1", "aaa", org);
		final Place t2 = this.createPlace("2", "aaa", org);
		final Place t3 = this.createPlace("3", "aaa", org);
		final Place s1 = this.createPlace("1", "bbb", org);
		final Place s2 = this.createPlace("2", "bbb", org);

		this.createSeasonPlace(season, t1);
		this.createSeasonPlace(season, t2);
		this.createSeasonPlace(season, t3);
		this.createSeasonPlace(season, s1);
		this.createSeasonPlace(season, s2);

		start = Date.from(now.plusYears(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		end = Date.from(now.plusYears(1).withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault())
				.minusMinutes(1).toInstant());

		final Season season2 = new Season();
		season2.setDateStart(start);
		season2.setDateEnd(end);
		season2.setTimeStart(4 * 60);
		season2.setTimeEnd(16 * 60);
		season2.setName("test 2");
		season2.setTypes("aaa");
		season2.setOrganization(org);
		this.seasonRepository.save(season2);
		this.createSeasonPlace(season2, t2);
		this.createSeasonPlace(season2, t3);

		final SeasonDetails current = this.seasonService.getDetailsCurrent(org.getName());
		Assert.assertNotNull(current);
		Assert.assertNotNull(current.getSeason());
		Assert.assertEquals(season.getTimeStart(), current.getSeason().getTimeStart());
		Assert.assertEquals(2, current.getPlaces().size());
		final PlacesInfo placesInfo0 = current.getPlaces().get(0);
		final PlacesInfo placesInfo1 = current.getPlaces().get(1);
		if ("aaa".equals(placesInfo0.getPlaceType())) {
			Assert.assertEquals(3, placesInfo0.getPlaceCount());
			Assert.assertEquals(2, placesInfo1.getPlaceCount());
		} else {
			Assert.assertEquals(3, placesInfo1.getPlaceCount());
			Assert.assertEquals(2, placesInfo0.getPlaceCount());
		}

		final SeasonDetails seasonDetails = this.seasonService.getDetailsByDate(org.getName(),
				Date.from(now.plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		Assert.assertNotNull(seasonDetails);
		Assert.assertNotNull(seasonDetails.getSeason());
		Assert.assertEquals(season2.getTimeStart(), seasonDetails.getSeason().getTimeStart());
		Assert.assertEquals(1, seasonDetails.getPlaces().size());
		Assert.assertEquals(2, seasonDetails.getPlaces().get(0).getPlaceCount());

		final SeasonDetails seasonDetails2 = this.seasonService.getDetailsByDate(org.getName(), season2.getDateStart());
		Assert.assertNotNull(seasonDetails2);
		Assert.assertNotNull(seasonDetails2.getSeason());
		Assert.assertEquals(seasonDetails.getSeason().getId(), seasonDetails2.getSeason().getId());

		final SeasonDetails seasonDetails3 = this.seasonService.getDetailsByDate(org.getName(), season2.getDateEnd());
		Assert.assertNotNull(seasonDetails3);
		Assert.assertNotNull(seasonDetails3.getSeason());
		Assert.assertEquals(seasonDetails.getSeason().getId(), seasonDetails3.getSeason().getId());

		@SuppressWarnings("deprecation")
		final SeasonDetails seasonDetails4 = this.seasonService.getDetailsByDate(org.getName(), new Date(1900, 1, 1));
		Assert.assertNull(seasonDetails4);

	}

	private Place createPlace(final String name, final String type, final Organization organization) {
		final Place place = new Place();
		place.setName(name);
		place.setType(type);
		place.setOrganization(organization);
		this.placeRepository.save(place);
		return place;
	}

	private void createSeasonPlace(final Season season, final Place place) {
		final SeasonPlace sp = new SeasonPlace();
		sp.setSeason(season);
		sp.setPlace(place);
		this.seasonPlaceRepository.save(sp);
	}
}
