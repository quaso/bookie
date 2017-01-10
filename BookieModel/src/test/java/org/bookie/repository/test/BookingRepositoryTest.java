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
import org.bookie.model.OrganizationUserRole;
import org.bookie.model.OwnerTimeSlot;
import org.bookie.model.Place;
import org.bookie.model.Role;
import org.bookie.model.Season;
import org.bookie.model.SeasonPlace;
import org.bookie.model.TimeSlot;
import org.bookie.model.User;
import org.bookie.repository.BookingRepositoryCustom;
import org.bookie.repository.OrganizationRepository;
import org.bookie.repository.OrganizationUserRoleRepository;
import org.bookie.repository.PlaceRepository;
import org.bookie.repository.RoleRepository;
import org.bookie.repository.SeasonPlaceRepository;
import org.bookie.repository.SeasonRepository;
import org.bookie.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
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

	@Autowired
	private OrganizationUserRoleRepository organizationUserRoleRepository;

	private final LocalDate now = LocalDate.now();
	private Organization org;
	private User user1, user2;
	private Place place1, place2;

	@Before
	public void init() {
		this.org = new Organization();
		this.org.setName("org");
		this.organizationRepository.save(this.org);

		final Role role = this.createRole("role");
		this.user1 = this.createUser("name1", role, this.org);
		this.user2 = this.createUser("name2", role, this.org);

		final Date start = this.date(this.now.withDayOfMonth(1).atStartOfDay());
		final Date end = this.date(this.now.withDayOfMonth(1).plusMonths(1).atStartOfDay().minusMinutes(1));

		final Season season = this.createSeason(start, end, "season1", "aaa,bbb", this.org);
		this.place1 = this.createPlace("p1", "aaa", this.org);
		this.createSeasonPlace(season, this.place1);
		this.place2 = this.createPlace("p2", "aaa", this.org);
		this.createSeasonPlace(season, this.place2);

		this.createBooking(this.now.atTime(12, 0), this.now.atTime(13, 0), "ttt", this.user1, season, this.place1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user1, season, this.place1);
		this.createBooking(this.now.atTime(13, 0), this.now.atTime(14, 0), "zzz", this.user1, season, this.place2);
		this.createBooking(this.now.atTime(10, 0), this.now.atTime(11, 0), "zzz", this.user2, season, this.place1);
	}

	@Test
	public void testFindOneTimeSlot() {
		// search by type (one result) timeslot only
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt"), null, null, TimeSlot.class);
		Assert.assertEquals(1, temp.size());
		Assert.assertNull(((Booking) temp.get(0)).getOwner());
	}

	@Test
	public void testFindOneOwnerTimeSlot() {
		// search by type (one result) timeslot only
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt"), null, null, OwnerTimeSlot.class);
		Assert.assertEquals(1, temp.size());
		Assert.assertNotNull(((Booking) temp.get(0)).getOwner());
		Assert.assertNull(((Booking) temp.get(0)).getType());
	}

	@Test
	public void testFindOneBooking() {
		// search by type (one result) whole booking
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt"), null, null, Booking.class);
		Assert.assertEquals(1, temp.size());
		Assert.assertNotNull(((Booking) temp.get(0)).getOwner());
		Assert.assertNotNull(((Booking) temp.get(0)).getType());
	}

	@Test
	public void testFindMore() {
		// search by type (more results)
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("zzz"), null, null, TimeSlot.class);
		Assert.assertEquals(3, temp.size());
	}

	@Test
	public void testFindMore2() {
		// search by more types (existing and non-existing)
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(11, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), null, null, TimeSlot.class);
		Assert.assertEquals(3, temp.size());
	}

	@Test
	public void testFindEmpty() {
		// search by time (no result)
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(11, 0)), this.date(this.now.atTime(12, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), null, null, TimeSlot.class);
		Assert.assertEquals(0, temp.size());
	}

	@Test
	public void testFindMiddle() {
		// search by time (two incomplete bookings)
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(13, 0)), this.date(this.now.atTime(13, 30)),
				Arrays.asList("ttt", "zzz", "yyy"), null, null, TimeSlot.class);
		Assert.assertEquals(2, temp.size());
	}

	@Test
	public void testFindUserId() {
		// search by userId
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), null, this.user2.getId(), TimeSlot.class);
		Assert.assertEquals(1, temp.size());
	}

	@Test
	public void testFindPlaceId() {
		// search by placeId
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), Arrays.asList(this.place1.getId()), null, TimeSlot.class);
		Assert.assertEquals(3, temp.size());
	}

	@Test
	public void testFindUserIdPlaceId() {
		// search by userId and placeId
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), Arrays.asList(this.place1.getId()), this.user1.getId(),
				TimeSlot.class);
		Assert.assertEquals(2, temp.size());
	}

	@Test
	public void testFindEmptyUserIdPlaceId() {
		// search by userId and placeId (no result)
		final List<? extends TimeSlot> temp = this.bookingRepository.find(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), Arrays.asList(this.place2.getId()), this.user2.getId(),
				TimeSlot.class);
		Assert.assertEquals(0, temp.size());
	}

	@Test
	public void testFree1() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(9, 0)));
		booking.setTimeEnd(this.date(this.now.atTime(9, 59)));
		Assert.assertTrue(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree2() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(9, 0)));
		booking.setTimeEnd(this.date(this.now.atTime(10, 0)));
		Assert.assertTrue(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree3() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(9, 0)));
		booking.setTimeEnd(this.date(this.now.atTime(10, 30)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree4() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(11, 0)));
		booking.setTimeEnd(this.date(this.now.atTime(12, 0)));
		Assert.assertTrue(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree5() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(11, 0)));
		booking.setTimeEnd(this.date(this.now.atTime(12, 01)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree6() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(12, 1)));
		booking.setTimeEnd(this.date(this.now.atTime(12, 2)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree7() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(12, 1)));
		booking.setTimeEnd(this.date(this.now.atTime(13, 0)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree8() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(12, 1)));
		booking.setTimeEnd(this.date(this.now.atTime(13, 1)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree9() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(13, 0)));
		booking.setTimeEnd(this.date(this.now.atTime(14, 0)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree10() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(13, 30)));
		booking.setTimeEnd(this.date(this.now.atTime(14, 30)));
		Assert.assertFalse(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree11() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(14, 0)));
		booking.setTimeEnd(this.date(this.now.atTime(14, 1)));
		Assert.assertTrue(this.bookingRepository.checkFreeTime(booking));
	}

	@Test
	public void testFree12() {
		final Booking booking = new Booking();
		booking.setPlace(this.place1);
		booking.setTimeStart(this.date(this.now.atTime(11, 59)));
		booking.setTimeEnd(this.date(this.now.atTime(17, 1)));
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

	private User createUser(final String name, final Role role, final Organization organization) {
		final User user = new User();
		user.setUsername(name);
		user.setName(name);
		user.setSurname("surname");
		user.setPhone("123");
		user.setPassword("pwd");
		this.userRepository.save(user);

		final OrganizationUserRole our = new OrganizationUserRole();
		our.setValues(organization, user, role);
		this.organizationUserRoleRepository.save(our);

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
