package org.bookie.test.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.bookie.exception.NotFreeException;
import org.bookie.model.Booking;
import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.model.Role;
import org.bookie.model.Season;
import org.bookie.model.SeasonPlace;
import org.bookie.model.TimeSlot;
import org.bookie.model.User;
import org.bookie.repository.BookingRepositoryCustom;
import org.bookie.repository.OrganizationRepository;
import org.bookie.repository.PlaceRepository;
import org.bookie.repository.RoleRepository;
import org.bookie.repository.SeasonPlaceRepository;
import org.bookie.repository.SeasonRepository;
import org.bookie.service.BookingService;
import org.bookie.service.UserService;
import org.bookie.test.TestConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles({ "test", "dbAuth" })
@Transactional
public class BookingServiceTest {

	private static final String USER_PWD = "pwd";

	@Autowired
	private BookingRepositoryCustom bookingRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

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

	@Autowired
	private ProviderManager providerManager;

	private final LocalDate now = LocalDate.now();
	private User user1, user2;
	private Season season;
	private Place t1, t2, t3;
	private Organization org;

	@Before
	public void init() {
		this.user1 = this.createUser("name1", this.createRole("role1"));
		this.user2 = this.createUser("name2", this.createRole("role2"));

		final Date start = Date.from(this.now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				this.now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1)
						.toInstant());

		this.org = new Organization();
		this.org.setName("org");
		this.organizationRepository.save(this.org);

		this.season = this.createSeason(start, end, "test", "aaa,bbb", this.org);

		this.t1 = this.createPlace("1", "aaa", this.org);
		this.t2 = this.createPlace("2", "aaa", this.org);
		this.t3 = this.createPlace("3", "aaa", this.org);

		this.createSeasonPlace(this.season, this.t1);
		this.createSeasonPlace(this.season, this.t2);
		this.createSeasonPlace(this.season, this.t3);

		SecurityContextHolder.getContext().setAuthentication(this.providerManager
				.authenticate(new UsernamePasswordAuthenticationToken(this.user1.getUsername(), USER_PWD)));
	}

	@Test
	public void testCreate1() throws NotFreeException {
		final Booking booking = this.bookingService.createBooking(
				this.date(this.now.atTime(12, 0)),
				this.date(this.now.atTime(13, 0)),
				"aaa",
				this.user1.getId(), this.t1.getId(), null);

		final Booking booking2 = this.bookingRepository.findOne(booking.getId());
		Assert.assertEquals(this.user1.getId(), booking2.getOwner().getId());
		Assert.assertEquals(this.t1.getId(), booking2.getPlace().getId());
		Assert.assertEquals(this.season.getId(), booking2.getSeason().getId());
	}

	@Test(expected = NotFreeException.class)
	public void testCreateNotFree1() throws NotFreeException {
		this.bookingService.createBooking(
				this.date(this.now.atTime(12, 0)),
				this.date(this.now.atTime(13, 0)),
				"aaa",
				this.user1.getId(), this.t1.getId(), null);
		this.bookingService.createBooking(
				this.date(this.now.atTime(12, 0)),
				this.date(this.now.atTime(13, 0)),
				"aaa",
				this.user1.getId(), this.t1.getId(), null);
	}

	@Test(expected = NotFreeException.class)
	public void testCreateNotFree2() throws NotFreeException {
		this.bookingService.createBooking(
				this.date(this.now.atTime(12, 0)),
				this.date(this.now.atTime(13, 0)),
				"aaa",
				this.user1.getId(), this.t1.getId(), null);
		this.bookingService.createBooking(
				this.date(this.now.atTime(12, 59)),
				this.date(this.now.atTime(13, 5)),
				"aaa",
				this.user1.getId(), this.t1.getId(), null);
	}

	@Test
	public void testCreate2() throws NotFreeException {
		this.bookingService.createBooking(
				this.date(this.now.atTime(12, 0)),
				this.date(this.now.atTime(13, 0)),
				"aaa",
				this.user1.getId(), this.t1.getId(), null);
		this.bookingService.createBooking(
				this.date(this.now.atTime(13, 0)),
				this.date(this.now.atTime(14, 0)),
				"aaa",
				this.user1.getId(), this.t1.getId(), null);
		this.bookingService.createBooking(
				this.date(this.now.atTime(13, 0)),
				this.date(this.now.atTime(14, 0)),
				"aaa",
				this.user1.getId(), this.t2.getId(), null);
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest1() {
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(this.org.getName(), 15, -10, 12 * 60, Arrays.asList(this.now))
						.size());
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest2() {
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(this.org.getName(), 15, 25 * 60, 12 * 60, Arrays.asList(this.now))
						.size());
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest3() {
		Assert.assertEquals(0,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 11 * 60 + 55, 25 * 60, Arrays.asList(this.now))
						.size());
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest4() {
		Assert.assertEquals(0,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 11 * 60 + 55, 11 * 60, Arrays.asList(this.now))
						.size());
	}

	@Test(expected = Exception.class)
	public void freeTimeSlotIllegalTest5() {
		// time slot cannot fit
		Assert.assertEquals(0,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 11 * 60 + 55, 12 * 60, Arrays.asList(this.now))
						.size());
	}

	@Test
	public void freeTimeSlot0DaysTest() {
		// no dates
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(this.org.getName(), 15, 11 * 60 + 55, 13 * 60, new ArrayList<>())
						.size());
	}

	@Test
	public void freeTimeSlot1DayTest() {
		// two bookings one after the other with no free space
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user1, this.t1);

		final Set<LocalDate> freeTimeSlots = this.bookingService.findFreeTimeSlots(this.org.getName(), 15, 11 * 60 + 55,
				14 * 60 + 5,
				Arrays.asList(this.now));
		Assert.assertNotNull(freeTimeSlots);
		Assert.assertEquals(0, freeTimeSlots.size());

		Assert.assertEquals(0,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 12 * 60 + 55, 13 * 60 + 55, Arrays.asList(this.now))
						.size());
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(this.org.getName(), 15, 12 * 60, 13 * 60, Arrays.asList(this.now))
						.size());
		// time slot too small to check all bookings
		Assert.assertEquals(0,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 12 * 60, 13 * 60 + 10, Arrays.asList(this.now))
						.size());
		// time slot just enough to check all bookings
		Assert.assertEquals(0,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 12 * 60, 13 * 60 + 15, Arrays.asList(this.now))
						.size());

		// free time before bookings
		Assert.assertEquals(1,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 11 * 60, 14 * 60 + 5, Arrays.asList(this.now))
						.size());
		Assert.assertEquals(1,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 11 * 60 + 45, 14 * 60 + 5, Arrays.asList(this.now))
						.size());
		// free time after bookings
		Assert.assertEquals(1,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 11 * 60 + 55, 14 * 60 + 15, Arrays.asList(this.now))
						.size());
		Assert.assertEquals(1,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 11 * 60 + 55, 15 * 60, Arrays.asList(this.now))
						.size());

		// two bookings one after the other with some free space
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.t2);
		this.createBooking(this.now.atTime(13, 30), this.now.atTime(14, 0), "zzz", this.user1, this.t2);

		Assert.assertEquals(1,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 15, 11 * 60 + 55, 14 * 60 + 5, Arrays.asList(this.now))
						.size());
		Assert.assertEquals(1,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 30, 11 * 60 + 55, 14 * 60 + 5, Arrays.asList(this.now))
						.size());
		Assert.assertEquals(0,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 45, 11 * 60 + 55, 14 * 60 + 5, Arrays.asList(this.now))
						.size());

		// one booking with large gap
		this.createBooking(this.now.atTime(10, 0), this.now.atTime(11, 0), "zzz", this.user1, this.t3);
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(this.org.getName(), 90, 9 * 60, 15 * 60, Arrays.asList(this.now))
						.size());
	}

	@Test
	public void freeTimeSlot2DaysTest() {
		// two bookings one after the other with no free space
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user1, this.t1);

		// two bookings one after the other with some free space
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.t2);
		this.createBooking(this.now.atTime(13, 30), this.now.atTime(14, 0), "zzz", this.user1, this.t2);

		// one booking with large gap
		this.createBooking(this.now.atTime(10, 0), this.now.atTime(11, 0), "zzz", this.user1, this.t3);

		// search in more days
		final LocalDate nextDay = this.now.plusDays(1);
		Assert.assertEquals(1,
				this.bookingService
						.findFreeTimeSlots(this.org.getName(), 45, 12 * 60, 14 * 60, Arrays.asList(this.now, nextDay))
						.size());
		Assert.assertEquals(2, this.bookingService
				.findFreeTimeSlots(this.org.getName(), 45, 12 * 60, 14 * 60,
						Arrays.asList(this.now, nextDay, nextDay.plusDays(1)))
				.size());

		this.createBooking(nextDay.atTime(12, 0), nextDay.atTime(13, 0), "ttt", this.user1, this.t2);
		Assert.assertEquals(1,
				this.bookingService.findFreeTimeSlots(this.org.getName(), 45, 11 * 60 + 55, 14 * 60 + 5,
						Arrays.asList(this.now, nextDay))
						.size());

		this.createBooking(nextDay.atTime(13, 30), nextDay.atTime(14, 0), "zzz", this.user1, this.t2);
		Assert.assertEquals(0,
				this.bookingService.findFreeTimeSlots(this.org.getName(), 45, 11 * 60 + 55, 14 * 60 + 5,
						Arrays.asList(this.now, nextDay))
						.size());
	}

	@Test
	public void testFindNoAuth() {
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user1, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user2, this.t2);

		SecurityContextHolder.getContext().setAuthentication(null);

		final List<? extends TimeSlot> temp = this.bookingService.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("zzz"), null, null);
		Assert.assertEquals(2, temp.size());
		Assert.assertNull(((Booking) temp.get(0)).getOwner());
		Assert.assertNull(((Booking) temp.get(1)).getOwner());
	}

	@Test
	public void testFindAuthUser() {
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user1, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user2, this.t2);

		SecurityContextHolder.getContext().setAuthentication(this.providerManager
				.authenticate(new UsernamePasswordAuthenticationToken(this.user1.getUsername(), USER_PWD)));

		final List<? extends TimeSlot> temp = this.bookingService.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("zzz"), null, null);
		Assert.assertEquals(2, temp.size());
		if (((Booking) temp.get(0)).getOwner() != null) {
			Assert.assertEquals(this.user1.getId(), ((Booking) temp.get(0)).getOwner().getId());
			Assert.assertNull(((Booking) temp.get(1)).getOwner());
		} else {
			Assert.assertEquals(this.user1.getId(), ((Booking) temp.get(1)).getOwner().getId());
			Assert.assertNull(((Booking) temp.get(0)).getOwner());
		}
	}

	@Test
	public void testFindAuthAdmin() {
		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user1, this.t1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user2, this.t2);

		final User admin = this.createUser("admin", this.createRole("ROLE_SUPER_ADMIN"));

		SecurityContextHolder.getContext().setAuthentication(this.providerManager
				.authenticate(new UsernamePasswordAuthenticationToken(admin.getUsername(), USER_PWD)));

		final List<? extends TimeSlot> temp = this.bookingService.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("zzz"), null, null);
		Assert.assertEquals(2, temp.size());
		Assert.assertNotNull(((Booking) temp.get(0)).getOwner());
		Assert.assertNotNull(((Booking) temp.get(1)).getOwner());
	}

	private Date date(final LocalDateTime ldt) {
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}

	private Booking createBooking(final LocalDateTime start, final LocalDateTime end, final String type,
			final User user, final Place place) {
		Booking booking = null;
		try {
			booking = this.bookingService.createBooking(this.date(start), this.date(end), type, user.getId(),
					place.getId(), null);
		} catch (final NotFreeException e) {
			Assert.fail(e.getMessage());
		}
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
		user.setPassword(USER_PWD);
		user.getRoles().add(role);
		user.setEnabled(true);
		this.userService.createUser(user);
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
