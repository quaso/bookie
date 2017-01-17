package org.bookie.test.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.bookie.model.BookingPattern;
import org.bookie.model.Season;
import org.bookie.test.conf.TestConfiguration;
import org.bookie.util.BookingPatternIterator;
import org.bookie.util.BookingPatternIterator.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles("test")
public class BookingPatternIteratorTest {

	@Test
	public void testOneDayInWeek_Count() {
		for (int count = 1; count <= 500; count++) {
			final BookingPattern pattern = this.createPattern(LocalDate.of(2017, 1, 1), LocalTime.of(7, 0),
					LocalTime.of(8, 0));
			pattern.getDays().add(DayOfWeek.MONDAY);
			pattern.setOccurenceCount(count);
			final BookingPatternIterator patternIterator = new BookingPatternIterator(pattern, null);
			final List<Interval> list = IteratorUtils.toList(patternIterator);
			Assert.assertEquals((int) pattern.getOccurenceCount(), list.size());

			LocalDate date = LocalDate.of(2017, 1, 2);
			for (int i = 0; i < count; i++) {
				Assert.assertEquals(date, list.get(i).getStart().toLocalDate());
				Assert.assertEquals(date, list.get(i).getEnd().toLocalDate());
				date = date.plusDays(7);
			}
		}
	}

	@Test
	public void testMoreDaysInWeek_Count() {
		final LocalDate[] dates = new LocalDate[] { LocalDate.of(2017, 1, 2), LocalDate.of(2017, 1, 4),
				LocalDate.of(2017, 1, 5), LocalDate.of(2017, 1, 7),
				LocalDate.of(2017, 1, 9), LocalDate.of(2017, 1, 11), LocalDate.of(2017, 1, 12),
				LocalDate.of(2017, 1, 14),
				LocalDate.of(2017, 1, 16), LocalDate.of(2017, 1, 18), LocalDate.of(2017, 1, 19),
				LocalDate.of(2017, 1, 21),
				LocalDate.of(2017, 1, 23), LocalDate.of(2017, 1, 25), LocalDate.of(2017, 1, 26),
				LocalDate.of(2017, 1, 28),
				LocalDate.of(2017, 1, 30), LocalDate.of(2017, 2, 1), LocalDate.of(2017, 2, 2),
				LocalDate.of(2017, 2, 4),
				LocalDate.of(2017, 2, 6) };

		for (int count = 1; count < dates.length; count++) {
			final BookingPattern pattern = this.createPattern(LocalDate.of(2017, 1, 2), LocalTime.of(7, 0),
					LocalTime.of(8, 0));
			pattern.getDays().add(DayOfWeek.MONDAY);
			pattern.getDays().add(DayOfWeek.WEDNESDAY);
			pattern.getDays().add(DayOfWeek.THURSDAY);
			pattern.getDays().add(DayOfWeek.SATURDAY);
			pattern.setOccurenceCount(count);
			final BookingPatternIterator patternIterator = new BookingPatternIterator(pattern, null);
			final List<Interval> list = IteratorUtils.toList(patternIterator);
			Assert.assertEquals((int) pattern.getOccurenceCount(), list.size());
			for (int i = 0; i < count; i++) {
				Assert.assertEquals(dates[i], list.get(i).getStart().toLocalDate());
				Assert.assertEquals(dates[i], list.get(i).getEnd().toLocalDate());
			}
		}
	}

	@Test
	public void testOneDayInWeek_HardEnd() {
		final BookingPattern pattern = this.createPattern(LocalDate.of(2017, 1, 1), LocalTime.of(7, 0),
				LocalTime.of(8, 0));
		pattern.getDays().add(DayOfWeek.MONDAY);
		pattern.setEndDate(LocalDate.of(2017, 12, 31));
		pattern.setWholeSeason(false);
		final Season season = new Season();
		season.setDateEnd(
				Date.from(LocalDate.of(2017, 8, 1).minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault())
						.toInstant()));
		final BookingPatternIterator patternIterator = new BookingPatternIterator(pattern, season);
		final List<Interval> list = IteratorUtils.toList(patternIterator);
		Assert.assertEquals(52, list.size());

		LocalDate date = LocalDate.of(2017, 1, 2);
		for (int i = 0; i < 52; i++) {
			Assert.assertEquals(date, list.get(i).getStart().toLocalDate());
			Assert.assertEquals(date, list.get(i).getEnd().toLocalDate());
			date = date.plusDays(7);
		}
	}

	@Test
	public void testOneDayInWeek_Season() {
		final BookingPattern pattern = this.createPattern(LocalDate.of(2017, 1, 1), LocalTime.of(7, 0),
				LocalTime.of(8, 0));
		pattern.getDays().add(DayOfWeek.MONDAY);
		pattern.setWholeSeason(true);
		final Season season = new Season();
		season.setDateEnd(
				Date.from(LocalDate.of(2017, 8, 1).minusDays(1).atStartOfDay().atZone(ZoneId.systemDefault())
						.toInstant()));
		final BookingPatternIterator patternIterator = new BookingPatternIterator(pattern, season);
		final List<Interval> list = IteratorUtils.toList(patternIterator);
		Assert.assertEquals(31, list.size());

		LocalDate date = LocalDate.of(2017, 1, 2);
		for (int i = 0; i < 31; i++) {
			Assert.assertEquals(date, list.get(i).getStart().toLocalDate());
			Assert.assertEquals(date, list.get(i).getEnd().toLocalDate());
			date = date.plusDays(7);
		}
	}

	@Test
	public void testEmptyDays() {
		final BookingPattern pattern = this.createPattern(LocalDate.of(2017, 1, 1), LocalTime.of(7, 0),
				LocalTime.of(8, 0));
		pattern.setOccurenceCount(10);
		final BookingPatternIterator patternIterator = new BookingPatternIterator(pattern, null);
		final List<Interval> list = IteratorUtils.toList(patternIterator);
		Assert.assertEquals(0, list.size());
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
