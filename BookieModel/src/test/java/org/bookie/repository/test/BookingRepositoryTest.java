package org.bookie.repository.test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bookie.model.Booking;
import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.model.Role;
import org.bookie.model.Season;
import org.bookie.model.TimeSlot;
import org.bookie.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BookingRepositoryTest extends AbstractTest {

	private final LocalDate now = LocalDate.now();
	private Organization org;
	private User user1, user2;
	private Place place1, place2;

	@Before
	public void init() {
		this.org = this.createOrganization("org");

		final Role role = this.createRole("role");
		this.user1 = this.createUser("name1", role, this.org);
		this.user2 = this.createUser("name2", role, this.org);

		final Date start = this.date(this.now.withDayOfMonth(1).atStartOfDay());
		final Date end = this.date(this.now.withDayOfMonth(1).plusMonths(1).atStartOfDay().minusMinutes(1));

		final Season season = this.createSeason(start, end, "season1", this.org);
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
		final List<? extends TimeSlot> temp = this.bookingRepository.findNoOwner(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt"), null, null);
		Assert.assertEquals(1, temp.size());
		Assert.assertNull(((Booking) temp.get(0)).getOwner());
	}

	@Test
	public void testFindOneOwnerTimeSlot() {
		// search by type (one result) timeslot only
		final List<? extends TimeSlot> temp = this.bookingRepository.findWithOwner(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt"), null, null);
		Assert.assertEquals(1, temp.size());
		Assert.assertNotNull(((Booking) temp.get(0)).getOwner());
		Assert.assertNull(((Booking) temp.get(0)).getType());
	}

	@Test
	public void testFindOneBooking() {
		// search by type (one result) whole booking
		final List<? extends TimeSlot> temp = this.bookingRepository.findBooking(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt"), null, null);
		Assert.assertEquals(1, temp.size());
		Assert.assertNotNull(((Booking) temp.get(0)).getOwner());
		Assert.assertNotNull(((Booking) temp.get(0)).getType());
	}

	@Test
	public void testFindMore() {
		// search by type (more results)
		final List<? extends TimeSlot> temp = this.bookingRepository.findNoOwner(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("zzz"), null, null);
		Assert.assertEquals(3, temp.size());
	}

	@Test
	public void testFindMore2() {
		// search by more types (existing and non-existing)
		final List<? extends TimeSlot> temp = this.bookingRepository.findNoOwner(this.org.getName(),
				this.date(this.now.atTime(11, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), null, null);
		Assert.assertEquals(3, temp.size());
	}

	@Test
	public void testFindEmpty() {
		// search by time (no result)
		final List<? extends TimeSlot> temp = this.bookingRepository.findNoOwner(this.org.getName(),
				this.date(this.now.atTime(11, 0)), this.date(this.now.atTime(12, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), null, null);
		Assert.assertEquals(0, temp.size());
	}

	@Test
	public void testFindMiddle() {
		// search by time (two incomplete bookings)
		final List<? extends TimeSlot> temp = this.bookingRepository.findNoOwner(this.org.getName(),
				this.date(this.now.atTime(13, 0)), this.date(this.now.atTime(13, 30)),
				Arrays.asList("ttt", "zzz", "yyy"), null, null);
		Assert.assertEquals(2, temp.size());
	}

	@Test
	public void testFindUserId() {
		// search by userId
		final List<? extends TimeSlot> temp = this.bookingRepository.findNoOwner(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), null, this.user2.getId());
		Assert.assertEquals(1, temp.size());
	}

	@Test
	public void testFindPlaceId() {
		// search by placeId
		final List<? extends TimeSlot> temp = this.bookingRepository.findNoOwner(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), Arrays.asList(this.place1.getId()), null);
		Assert.assertEquals(3, temp.size());
	}

	@Test
	public void testFindUserIdPlaceId() {
		// search by userId and placeId
		final List<? extends TimeSlot> temp = this.bookingRepository.findNoOwner(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), Arrays.asList(this.place1.getId()), this.user1.getId());
		Assert.assertEquals(2, temp.size());
	}

	@Test
	public void testFindEmptyUserIdPlaceId() {
		// search by userId and placeId (no result)
		final List<? extends TimeSlot> temp = this.bookingRepository.findNoOwner(this.org.getName(),
				this.date(this.now.atTime(10, 0)), this.date(this.now.atTime(14, 0)),
				Arrays.asList("ttt", "zzz", "yyy"), Arrays.asList(this.place2.getId()), this.user2.getId());
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
}
