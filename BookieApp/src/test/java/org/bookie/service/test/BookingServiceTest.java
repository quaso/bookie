package org.bookie.service.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

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
import org.bookie.service.BookingService;
import org.bookie.test.TestConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles("test")
@Transactional
public class BookingServiceTest {

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

	@Autowired
	private BookingService bookingService;

	private final LocalDate now = LocalDate.now();
	private User user1;
	private Season season;
	private Place t1, t2, t3;

	@Before
	public void init() {
		final Role role = this.createRole("role");
		this.user1 = this.createUser("name1", role);

		final Date start = Date.from(this.now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				this.now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1)
						.toInstant());

		final Organization org = new Organization();
		org.setName("org");
		this.organizationRepository.save(org);

		this.season = this.createSeason(start, end, "test", "aaa,bbb", org);

		this.t1 = this.createPlace("1", "aaa", org);
		this.t2 = this.createPlace("2", "aaa", org);
		this.t3 = this.createPlace("3", "aaa", org);

		this.createSeasonPlace(this.season, this.t1);
		this.createSeasonPlace(this.season, this.t2);
		this.createSeasonPlace(this.season, this.t3);
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest1() {
		Assert.assertEquals(0, this.bookingService.findFreeTimeSlots(15, -10, 12 * 60, Arrays.asList(this.now)).size());
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest2() {
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(15, 25 * 60, 12 * 60, Arrays.asList(this.now)).size());
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest3() {
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(15, 11 * 60 + 55, 25 * 60, Arrays.asList(this.now)).size());
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest4() {
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(15, 11 * 60 + 55, 11 * 60, Arrays.asList(this.now)).size());
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest5() {
		// time slot cannot fit
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(15, 11 * 60 + 55, 12 * 60, Arrays.asList(this.now)).size());
	}

	@Test
	public void freeTimeSlot0DaysTest() {
		// no dates
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(15, 11 * 60 + 55, 13 * 60, new ArrayList<>()).size());
	}

	@Test
	public void freeTimeSlot1DayTest() {
		// two bookings one after the other with no free space
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.season, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user1, this.season, this.t1);

		final Set<LocalDate> freeTimeSlots = this.bookingService.findFreeTimeSlots(15, 11 * 60 + 55, 14 * 60 + 5,
				Arrays.asList(this.now));
		Assert.assertNotNull(freeTimeSlots);
		Assert.assertEquals(0, freeTimeSlots.size());

		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(15, 12 * 60 + 55, 13 * 60 + 55, Arrays.asList(this.now)).size());
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(15, 12 * 60, 13 * 60, Arrays.asList(this.now)).size());

		// free time before bookings
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(15, 11 * 60, 14 * 60 + 5, Arrays.asList(this.now)).size());
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(15, 11 * 60 + 45, 14 * 60 + 5, Arrays.asList(this.now)).size());
		// free time after bookings
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(15, 11 * 60 + 55, 14 * 60 + 15, Arrays.asList(this.now)).size());
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(15, 11 * 60 + 55, 15 * 60, Arrays.asList(this.now)).size());

		// two bookings one after the other with some free space
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.season, this.t2);
		this.createBooking(this.now.atTime(13, 30), this.now.atTime(14, 0), "zzz", this.user1, this.season, this.t2);

		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(15, 11 * 60 + 55, 14 * 60 + 5, Arrays.asList(this.now)).size());
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(30, 11 * 60 + 55, 14 * 60 + 5, Arrays.asList(this.now)).size());
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(45, 11 * 60 + 55, 14 * 60 + 5, Arrays.asList(this.now)).size());

		// one booking with large gap
		this.createBooking(this.now.atTime(10, 0), this.now.atTime(11, 0), "zzz", this.user1, this.season, this.t3);
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(90, 9 * 60, 15 * 60, Arrays.asList(this.now)).size());
	}

	@Test
	public void freeTimeSlot2DaysTest() {
		// two bookings one after the other with no free space
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.season, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user1, this.season, this.t1);

		// two bookings one after the other with some free space
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.season, this.t2);
		this.createBooking(this.now.atTime(13, 30), this.now.atTime(14, 0), "zzz", this.user1, this.season, this.t2);

		// one booking with large gap
		this.createBooking(this.now.atTime(10, 0), this.now.atTime(11, 0), "zzz", this.user1, this.season, this.t3);

		// search in more days
		final LocalDate nextDay = this.now.plusDays(1);
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(45, 12 * 60, 14 * 60, Arrays.asList(this.now, nextDay)).size());
		Assert.assertEquals(2, this.bookingService
				.findFreeTimeSlots(45, 12 * 60, 14 * 60, Arrays.asList(this.now, nextDay, nextDay.plusDays(1))).size());

		this.createBooking(nextDay.atTime(12, 0), nextDay.atTime(13, 0), "ttt", this.user1, this.season, this.t2);
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(45, 11 * 60 + 55, 14 * 60 + 5, Arrays.asList(this.now, nextDay))
						.size());

		this.createBooking(nextDay.atTime(13, 30), nextDay.atTime(14, 0), "zzz", this.user1, this.season, this.t2);
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(45, 11 * 60 + 55, 14 * 60 + 5, Arrays.asList(this.now, nextDay))
						.size());
	}

	private Booking createBooking(final LocalDateTime start, final LocalDateTime end, final String type,
			final User user, @SuppressWarnings("hiding") final Season season, final Place place) {
		final Booking booking = new Booking();
		booking.setTimeStart(Date.from(start.atZone(ZoneId.systemDefault()).toInstant()));
		booking.setTimeEnd(Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));
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
		user.setName(name);
		user.setSurname("surname");
		user.setRole(role);
		this.userRepository.save(user);
		return user;
	}

	private Season createSeason(final Date start, final Date end, final String name, final String types,
			final Organization organization) {
		final Season result = new Season();
		result.setDateStart(start);
		result.setDateEnd(end);
		result.setTimeStart(7 * 60);
		result.setTimeEnd(22 * 60);
		result.setName(name);
		result.setTypes(types);
		result.setOrganization(organization);
		this.seasonRepository.save(result);
		return result;
	}

	private void createSeasonPlace(@SuppressWarnings("hiding") final Season season, final Place place) {
		final SeasonPlace sp = new SeasonPlace();
		sp.setSeason(season);
		sp.setPlace(place);
		this.seasonPlaceRepository.save(sp);
	}
}
