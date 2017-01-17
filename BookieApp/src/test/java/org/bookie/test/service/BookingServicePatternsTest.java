package org.bookie.test.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.bookie.exception.NotFreeException;
import org.bookie.model.Booking;
import org.bookie.model.BookingPattern;
import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.model.Season;
import org.bookie.model.User;
import org.bookie.service.BookingService;
import org.bookie.test.AbstractTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class BookingServicePatternsTest extends AbstractTest {

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
		this.org = this.createOrganization("org1");

		this.user1 = this.createUser("name1", this.createRole("role1"), this.org);
		this.user2 = this.createUser("name2", this.createRole("role2"), this.org);

		final Date start = Date.from(this.now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(this.now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault())
				.minusMinutes(1).toInstant());

		this.season = this.createSeason(start, end, "test", this.org);

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
	public void testBasic() throws NotFreeException {
		final BookingPattern pattern = this.createPattern(LocalDate.of(2017, 1, 1), LocalTime.of(7, 0),
				LocalTime.of(8, 0));
		pattern.getDays().add(DayOfWeek.MONDAY);
		pattern.setOccurenceCount(10);

		final List<Booking> bookings = this.bookingService.createBooking(pattern, "aaa", this.user1.getId(),
				this.t1.getId(), null);
		Assert.assertNotNull(bookings);
		Assert.assertEquals((int) pattern.getOccurenceCount(), bookings.size());
	}

	@Test
	public void testConflict() throws NotFreeException {
		BookingPattern pattern;
		pattern = this.createPattern(LocalDate.of(2017, 1, 16), LocalTime.of(7, 0),
				LocalTime.of(8, 0));
		pattern.setEndDate(pattern.getStartDate());
		this.bookingService.createBooking(pattern, "aaa", this.user2.getId(), this.t1.getId(), null);

		pattern = this.createPattern(LocalDate.of(2017, 1, 1), LocalTime.of(7, 0),
				LocalTime.of(8, 0));
		pattern.getDays().add(DayOfWeek.MONDAY);
		pattern.setOccurenceCount(10);
		try {
			this.bookingService.createBooking(pattern, "aaa", this.user1.getId(), this.t1.getId(), null);
			Assert.fail("NotFreeException was not thrown");
		} catch (final NotFreeException ex) {
		}
	}

	private BookingPattern createPattern(final LocalDate startDate, final LocalTime startTime,
			final LocalTime endTime) {
		final BookingPattern pattern = new BookingPattern();
		pattern.setStartDate(startDate);
		pattern.setTimeStart(startTime);
		pattern.setTimeEnd(endTime);
		return pattern;
	}
}
