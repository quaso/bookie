package org.bookie.repository.test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.bookie.model.Booking;
import org.bookie.model.Place;
import org.bookie.model.Role;
import org.bookie.model.Season;
import org.bookie.model.SeasonPlace;
import org.bookie.model.User;
import org.bookie.repository.BookingRepositoryCustom;
import org.bookie.repository.PlaceRepository;
import org.bookie.repository.RoleRepository;
import org.bookie.repository.SeasonPlaceRepository;
import org.bookie.repository.SeasonRepository;
import org.bookie.repository.UserRepository;
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
public class BookingTest {

	@Autowired
	private BookingRepositoryCustom bookingRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private SeasonRepository seasonRepository;

	@Autowired
	private SeasonPlaceRepository seasonPlaceRepository;

	@Test
	public void testFind() {
		final Role role = this.createRole("role");
		final User user1 = this.createUser("name1", role);
		final User user2 = this.createUser("name2", role);

		final LocalDate now = LocalDate.now();
		final Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1).toInstant());

		final Season season = this.createSeason(start, end, "season1", "aaa,bbb");
		final Place place1 = this.createPlace("p1", "aaa");
		this.createSeasonPlace(season, place1);
		final Place place2 = this.createPlace("p2", "aaa");
		this.createSeasonPlace(season, place2);

		this.createBooking(Date.from(now.atTime(12, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(13, 0).toInstant(ZoneOffset.UTC)), "ttt", user1, season, place1);
		this.createBooking(Date.from(now.atTime(13, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(14, 0).toInstant(ZoneOffset.UTC)), "zzz", user1, season, place1);
		this.createBooking(Date.from(now.atTime(13, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(14, 0).toInstant(ZoneOffset.UTC)), "zzz", user1, season, place2);
		this.createBooking(Date.from(now.atTime(10, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(11, 0).toInstant(ZoneOffset.UTC)), "zzz", user2, season, place1);

		List<Booking> temp;
		// search by type (one result)
		temp = this.bookingRepository.find(Date.from(now.atTime(10, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(14, 0).toInstant(ZoneOffset.UTC)), Arrays.asList(new String[] { "ttt" }), null,
				null);
		Assert.assertEquals(1, temp.size());

		// search by type (more results)
		temp = this.bookingRepository.find(Date.from(now.atTime(10, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(14, 0).toInstant(ZoneOffset.UTC)), Arrays.asList(new String[] { "zzz" }), null,
				null);
		Assert.assertEquals(3, temp.size());

		// search by more types (existing and non-existing)
		temp = this.bookingRepository.find(Date.from(now.atTime(11, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(14, 0).toInstant(ZoneOffset.UTC)),
				Arrays.asList(new String[] { "ttt", "zzz", "yyy" }), null, null);
		Assert.assertEquals(3, temp.size());

		// search by time (no result)
		temp = this.bookingRepository.find(Date.from(now.atTime(13, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(13, 30).toInstant(ZoneOffset.UTC)),
				Arrays.asList(new String[] { "ttt", "zzz", "yyy" }), null, null);
		Assert.assertEquals(0, temp.size());

		// search by userId
		temp = this.bookingRepository.find(Date.from(now.atTime(10, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(14, 0).toInstant(ZoneOffset.UTC)),
				Arrays.asList(new String[] { "ttt", "zzz", "yyy" }), null, user2.getId());
		Assert.assertEquals(1, temp.size());

		// search by placeId
		temp = this.bookingRepository.find(Date.from(now.atTime(10, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(14, 0).toInstant(ZoneOffset.UTC)),
				Arrays.asList(new String[] { "ttt", "zzz", "yyy" }), Arrays.asList(place1.getId()), null);
		Assert.assertEquals(3, temp.size());

		// search by userId and placeId
		temp = this.bookingRepository.find(Date.from(now.atTime(10, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(14, 0).toInstant(ZoneOffset.UTC)),
				Arrays.asList(new String[] { "ttt", "zzz", "yyy" }), Arrays.asList(place1.getId()), user1.getId());
		Assert.assertEquals(2, temp.size());

		// search by userId and placeId (no result)
		temp = this.bookingRepository.find(Date.from(now.atTime(10, 0).toInstant(ZoneOffset.UTC)),
				Date.from(now.atTime(14, 0).toInstant(ZoneOffset.UTC)),
				Arrays.asList(new String[] { "ttt", "zzz", "yyy" }), Arrays.asList(place2.getId()), user2.getId());
		Assert.assertEquals(0, temp.size());
	}

	private Booking createBooking(final Date start, final Date end, final String type, final User user,
			final Season season, final Place place) {
		final Booking booking = new Booking();
		booking.setTimeStart(start);
		booking.setTimeEnd(end);
		booking.setType(type);
		booking.setSeason(season);
		booking.setCreatedAt(new Date());
		booking.setPlace(place);
		booking.setOwner(user);
		booking.setCreatedBy(user);
		this.bookingRepository.save(booking);
		return booking;
	}

	private Place createPlace(final String name, final String type) {
		final Place place = new Place();
		place.setName(name);
		place.setType(type);
		this.placeRepository.save(place);
		return place;
	}

	private Role createRole(final String name) {
		final Role role = new Role();
		role.setName(name);
		this.roleRepository.save(role);
		return role;
	}

	private User createUser(final String name, final Role role) {
		final User user = new User();
		user.setName(name);
		user.setSurname("surname");
		user.setRole(role);
		this.userRepository.save(user);
		return user;
	}

	private Season createSeason(final Date start, final Date end, final String name, final String types) {
		final Season season = new Season();
		season.setDateStart(start);
		season.setDateEnd(end);
		season.setTimeStart(7 * 60);
		season.setTimeEnd(22 * 60);
		season.setName(name);
		season.setTypes(types);
		this.seasonRepository.save(season);
		return season;
	}

	private void createSeasonPlace(final Season season, final Place place) {
		final SeasonPlace sp = new SeasonPlace();
		sp.setSeason(season);
		sp.setPlace(place);
		this.seasonPlaceRepository.save(sp);
	}
}