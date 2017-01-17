package org.bookie.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

public class BookingPattern {

	private LocalTime timeStart;
	private LocalTime timeEnd;

	private final Set<DayOfWeek> days = new HashSet<>();

	private LocalDate startDate;
	private Boolean wholeSeason;
	private LocalDate endDate;
	private Integer occurenceCount;

	public boolean isReccurent() {
		return !this.days.isEmpty();
	}

	public LocalTime getTimeStart() {
		return this.timeStart;
	}

	public void setTimeStart(final LocalTime timeStart) {
		this.timeStart = timeStart;
	}

	public LocalTime getTimeEnd() {
		return this.timeEnd;
	}

	public void setTimeEnd(final LocalTime timeEnd) {
		this.timeEnd = timeEnd;
	}

	public LocalDate getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final LocalDate startDate) {
		this.startDate = startDate;
	}

	public Boolean isWholeSeason() {
		return this.wholeSeason;
	}

	public void setWholeSeason(final Boolean wholeSeason) {
		this.wholeSeason = wholeSeason;
	}

	public LocalDate getEndDate() {
		return this.endDate;
	}

	public void setEndDate(final LocalDate endDate) {
		this.endDate = endDate;
	}

	public Integer getOccurenceCount() {
		return this.occurenceCount;
	}

	public void setOccurenceCount(final Integer occurenceCount) {
		this.occurenceCount = occurenceCount;
	}

	public Set<DayOfWeek> getDays() {
		return this.days;
	}

}
