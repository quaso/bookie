package org.bookie.repository.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.bookie.model.Booking;
import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.model.Role;
import org.bookie.model.Season;
import org.bookie.model.SeasonPlace;
import org.bookie.model.User;
import org.bookie.repository.BookingRepositoryCustom;
import org.bookie.repository.OrganizationRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainTestConfiguration.class)
@ActiveProfiles("test")
@Transactional
@Rollback(false)
public class BookingRepositoryTest {

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

	@Autowired
	private OrganizationRepository organizationRepository;

	@Test
	public void testFind() {
		final Role role = this.createRole("role");
		final User user1 = this.createUser("name1", role);
		final User user2 = this.createUser("name2", role);

		final Organization org = new Organization();
		org.setName("org");
		this.organizationRepository.save(org);

		final LocalDate now = LocalDate.now();
		final Date start = this.date(now.withDayOfMonth(1).atStartOfDay());
		final Date end = this.date(now.withDayOfMonth(1).plusMonths(1).atStartOfDay().minusMinutes(1));

		final Season season = this.createSeason(start, end, "season1", "aaa,bbb", org);
		final Place place1 = this.createPlace("p1", "aaa", org);
		this.createSeasonPlace(season, place1);
		final Place place2 = this.createPlace("p2", "aaa", org);
		this.createSeasonPlace(season, place2);

		this.createBooking(now.atTime(12, 0), now.atTime(13, 0), "ttt", user1, season, place1);
		this.createBooking(now.atTime(13, 0), now.atTime(14, 0), "zzz", user1, season, place1);
		this.createBooking(now.atTime(13, 0), now.atTime(14, 0), "zzz", user1, season, place2);
		this.createBooking(now.atTime(10, 0), now.atTime(11, 0), "zzz", user2, season, place1);

		List<Booking> temp;
		// search by type (one result)
		temp = this.bookingRepository.find(this.date(now.atTime(10, 0)), this.date(now.atTime(14, 0)),
				Arrays.asList("ttt"), null, null);
		Assert.assertEquals(1, temp.size());

		// search by type (more results)
		temp = this.bookingRepository.find(this.date(now.atTime(10, 0)), this.date(now.atTime(14, 0)),
				Arrays.asList("zzz"), null, null);
		Assert.assertEquals(3, temp.size());

		// search by more types (existing and non-existing)
		temp = this.bookingRepository.find(this.date(now.atTime(11, 0)), this.date(now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), null, null);
		Assert.assertEquals(3, temp.size());

		// search by time (no result)
		temp = this.bookingRepository.find(this.date(now.atTime(11, 0)), this.date(now.atTime(12, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), null, null);
		Assert.assertEquals(0, temp.size());

		// search by time (two incomplete bookings)
		temp = this.bookingRepository.find(this.date(now.atTime(13, 0)), this.date(now.atTime(13, 30)),
				Arrays.asList("ttt", "zzz", "yyy"), null, null);
		Assert.assertEquals(2, temp.size());

		// search by userId
		temp = this.bookingRepository.find(this.date(now.atTime(10, 0)), this.date(now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), null, user2.getId());
		Assert.assertEquals(1, temp.size());

		// search by placeId
		temp = this.bookingRepository.find(this.date(now.atTime(10, 0)), this.date(now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), Arrays.asList(place1.getId()), null);
		Assert.assertEquals(3, temp.size());

		// search by userId and placeId
		temp = this.bookingRepository.find(this.date(now.atTime(10, 0)), this.date(now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), Arrays.asList(place1.getId()), user1.getId());
		Assert.assertEquals(2, temp.size());

		// search by userId and placeId (no result)
		temp = this.bookingRepository.find(this.date(now.atTime(10, 0)), this.date(now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), Arrays.asList(place2.getId()), user2.getId());
		Assert.assertEquals(0, temp.size());
	}

	//	@Test
	public void testFree() {
		final Role role = this.createRole("role");
		final User user1 = this.createUser("name1", role);
		final User user2 = this.createUser("name2", role);

		final Organization org = new Organization();
		org.setName("org");
		this.organizationRepository.save(org);

		final LocalDate now = LocalDate.now();
		final Date start = this.date(now.withDayOfMonth(1).atStartOfDay());
		final Date end = this.date(now.withDayOfMonth(1).plusMonths(1).atStartOfDay().minusMinutes(1));

		final Season season = this.createSeason(start, end, "season1", "aaa,bbb", org);
		final Place place1 = this.createPlace("p1", "aaa", org);
		this.createSeasonPlace(season, place1);
		final Place place2 = this.createPlace("p2", "aaa", org);
		this.createSeasonPlace(season, place2);

		this.createBooking(now.atTime(12, 0), now.atTime(13, 0), "ttt", user1, season, place1);
		this.createBooking(now.atTime(13, 0), now.atTime(14, 0), "zzz", user1, season, place1);
		this.createBooking(now.atTime(13, 0), now.atTime(14, 0), "zzz", user1, season, place2);
		this.createBooking(now.atTime(10, 0), now.atTime(11, 0), "zzz", user2, season, place1);

		final Booking booking = new Booking();
		booking.setPlace(place1);

		booking.setTimeStart(this.date(now.atTime(9, 0)));
		booking.setTimeEnd(this.date(now.atTime(9, 59)));
		Assert.assertTrue(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(9, 0)));
		booking.setTimeEnd(this.date(now.atTime(10, 0)));
		Assert.assertTrue(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(9, 0)));
		booking.setTimeEnd(this.date(now.atTime(10, 30)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(11, 0)));
		booking.setTimeEnd(this.date(now.atTime(12, 0)));
		Assert.assertTrue(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(11, 0)));
		booking.setTimeEnd(this.date(now.atTime(12, 01)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(12, 1)));
		booking.setTimeEnd(this.date(now.atTime(12, 2)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(12, 1)));
		booking.setTimeEnd(this.date(now.atTime(13, 0)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(12, 1)));
		booking.setTimeEnd(this.date(now.atTime(13, 1)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(13, 0)));
		booking.setTimeEnd(this.date(now.atTime(14, 0)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(13, 30)));
		booking.setTimeEnd(this.date(now.atTime(14, 30)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(14, 0)));
		booking.setTimeEnd(this.date(now.atTime(14, 1)));
		Assert.assertTrue(this.bookingRepository.checkFreeTime(booking));

		booking.setTimeStart(this.date(now.atTime(11, 59)));
		booking.setTimeEnd(this.date(now.atTime(17, 1)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));
	}

	private Date date(final LocalDateTime ldt) {
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}

	private Booking createBooking(final LocalDateTime start, final LocalDateTime end, final String type,
			final User user, final Season season, final Place place) {
		final Booking booking = new Booking();
		booking.setTimeStart(this.date(start));
		booking.setTimeEnd(this.date(end));
		booking.setType(type);
		booking.setSeason(season);
		booking.setCreatedAt(new Date());
		booking.setPlace(place);
		booking.setOwner(user);
		booking.setCreatedBy(user);
		this.bookingRepository.save(booking);
		return booking;
	}

	private Place createPlace(final String name, final String type, final Organization organization) {
		final Place place = new Place();
		place.setName(name);
		place.setType(type);
		place.setOrganization(organization);
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
		user.setUsername(name);
		user.setName(name);
		user.setSurname("surname");
		user.setPhone("123");
		user.setPassword("pwd");
		user.getRoles().add(role);
		this.userRepository.save(user);
		return user;
	}

	private Season createSeason(final Date start, final Date end, final String name, final String types,
			final Organization organization) {
		final Season season = new Season();
		season.setDateStart(start);
		season.setDateEnd(end);
		season.setTimeStart(7 * 60);
		season.setTimeEnd(22 * 60);
		season.setName(name);
		season.setTypes(types);
		season.setOrganization(organization);
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
