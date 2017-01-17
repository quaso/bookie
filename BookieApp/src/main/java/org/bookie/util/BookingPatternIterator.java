package org.bookie.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Iterator;

import org.bookie.model.BookingPattern;
import org.bookie.model.Season;
import org.bookie.util.BookingPatternIterator.Interval;

public class BookingPatternIterator implements Iterator<Interval> {

	private final BookingPattern pattern;
	private final LocalDate endDate;

	private LocalDate last;
	private int count;
	private Interval nextBuff;

	public BookingPatternIterator(final BookingPattern pattern, final Season season) {
		this.pattern = pattern;
		this.endDate = pattern.isWholeSeason() != null && pattern.isWholeSeason()
				? LocalDate.from(season.getDateEnd().toInstant().atZone(ZoneId.systemDefault())) : pattern.getEndDate();
		this.last = pattern.getStartDate().minusDays(1);
		this.count = 0;
	}

	@Override
	public boolean hasNext() {
		this.nextBuff = this.computeNext();
		return this.nextBuff != null;
	}

	@Override
	public Interval next() {
		Interval result;
		if (this.nextBuff != null) {
			result = this.nextBuff;
			this.nextBuff = null;
		} else {
			result = this.computeNext();
		}
		this.last = result.getStart().toLocalDate();
		this.count++;
		return result;
	}

	private Interval computeNext() {
		Interval result = null;
		if (!this.pattern.getDays().isEmpty()) {
			if (this.pattern.getOccurenceCount() == null || this.pattern.getOccurenceCount() > this.count) {
				LocalDate date = this.last;

				do {
					date = date.plusDays(1);
					if (!this.pattern.getDays().contains(date.getDayOfWeek())) {
						continue;
					}
					if (this.endDate != null && date.isAfter(this.endDate)) {
						break;
					}
					result = new Interval(date, this.pattern.getTimeStart(), this.pattern.getTimeEnd());

				} while (result == null);
			}
		}
		return result;
	}

	public static class Interval {
		private final LocalDateTime start;
		private final LocalDateTime end;

		public Interval(final LocalDate date, final LocalTime timeStart, final LocalTime timeEnd) {
			this.start = date.atTime(timeStart);
			this.end = date.atTime(timeEnd);
		}

		public LocalDateTime getStart() {
			return this.start;
		}

		public LocalDateTime getEnd() {
			return this.end;
		}
	}

}
