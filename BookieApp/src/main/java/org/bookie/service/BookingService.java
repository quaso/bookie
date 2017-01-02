package org.bookie.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.bookie.exception.NotFreeException;
import org.bookie.model.Booking;
import org.bookie.model.Place;
import org.bookie.model.Season;
import org.bookie.model.User;
import org.bookie.repository.BookingRepositoryCustom;
import org.bookie.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

	@Autowired
	private BookingRepositoryCustom bookingRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private SeasonService seasonService;

	@Transactional
	public Booking createBooking(final Date timeStart, final Date timeEnd, final String type, final String ownerId,
			final String placeId, final String note) throws NotFreeException {
		if (timeStart.after(timeEnd)) {
			throw new IllegalArgumentException("Start must be before end");
		}
		if (!this.checkSameDay(timeStart, timeEnd)) {
			throw new IllegalStateException("Start and end dates must be in the same day");
		}

		final User owner = this.userService.findById(ownerId);
		if (owner == null) {
			throw new IllegalStateException("Owner could not be identified");
		}

		final Place place = this.placeRepository.findOne(placeId);
		if (place == null) {
			throw new IllegalStateException("Place could not be identified");
		}

		// TODO: check if organization is loaded
		final Season season = this.seasonService.getByDate(place.getOrganization().getName(), timeStart);
		if (season == null) {
			throw new IllegalStateException("No season is defined for requested booking date");
		}

		final Booking booking = new Booking();
		booking.setTimeStart(timeStart);
		booking.setTimeEnd(timeEnd);
		booking.setSeason(season);
		booking.setOwner(owner);
		booking.setPlace(place);
		booking.setCreatedAt(new Date());
		booking.setCreatedBy(owner);
		// TODO: auditing
		// booking.setCreatedBy(createdBy);
		booking.setType(type);
		booking.setNote(note);

		// check if the datetime is free
		if (!this.bookingRepository.checkFreeTime(booking)) {
			throw new NotFreeException();
		}
		this.bookingRepository.save(booking);
		return booking;
	}

	public void delete(final String bookingId) {
		this.bookingRepository.delete(bookingId);
	}

	// public List<Booking> getAll(final Date dateStart, final Date
	// dateEnd,final String type, final String placeId, final String ownerId){
	// this.bookingRepository.fi
	// }

	private boolean checkSameDay(final Date timeStart, final Date timeEnd) {
		final LocalDate lds = LocalDate.from(timeStart.toInstant());
		final LocalDate lde = LocalDate.from(timeEnd.toInstant());
		return lds.equals(lde);
	}

	// TODO: create tests
	public Set<LocalDate> findFreeTimeSlots(final int duration, final int minutesStart, final int minutesEnd,
			final Collection<LocalDate> days) {
		if (minutesStart < 0 || minutesStart > 24 * 60) {
			throw new IllegalArgumentException("TimeStart");
		}
		if (minutesEnd < minutesStart || minutesEnd > 24 * 60) {
			throw new IllegalArgumentException("TimeEnd");
		}
		if (minutesEnd - minutesStart < duration) {
			throw new IllegalArgumentException("Duration");
		}

		final Set<LocalDate> result = new HashSet<>();
		for (final LocalDate date : days) {
			final Date start = Date
					.from(date.atStartOfDay().plusMinutes(minutesStart).atZone(ZoneId.systemDefault()).toInstant());
			final Date end = Date
					.from(date.atStartOfDay().plusMinutes(minutesEnd).atZone(ZoneId.systemDefault()).toInstant());
			// get existing bookings which are in desired time frame
			final List<Booking> bookings = this.bookingRepository.find(start, end, null, null, null);

			if (bookings.isEmpty()) {
				// there are no booking at this day
				result.add(date);
			} else {
				// find unique placeIds
				final List<String> placeIds = bookings.stream().map(b -> b.getPlace().getId()).distinct()
						.collect(Collectors.toList());

				// for each placeId evaluate if there is free time slot (or until
				// first match)
				for (final String pid : placeIds) {
					// get bookings just for the placeId
					final List<Booking> bookingsForPlace = bookings.stream()
							.filter(b -> pid.equals(b.getPlace().getId()))
							.collect(Collectors.toList());

					boolean found = false;
					LocalDateTime timeEnd = date.atStartOfDay().plusMinutes(minutesStart);
					for (final Booking booking : bookingsForPlace) {
						timeEnd = timeEnd.plusMinutes(duration);
						final int expectedEndMinutes = timeEnd.getHour() * 60 + timeEnd.getMinute();
						if (expectedEndMinutes > minutesEnd || expectedEndMinutes > 24 * 60) {
							// expected end is after desired end or after midnight
							break;
						}
						final LocalDateTime timeStart = LocalDateTime.ofInstant(booking.getTimeStart().toInstant(),
								ZoneId.systemDefault());
						if (timeStart.compareTo(timeEnd) >= 0) {
							// time slot found
							found = true;
							break;
						}
						// get end of the booking
						timeEnd = LocalDateTime.ofInstant(booking.getTimeEnd().toInstant(), ZoneId.systemDefault());
					}

					timeEnd = timeEnd.plusMinutes(duration);
					final int expectedEndMinutes = timeEnd.getHour() * 60 + timeEnd.getMinute();
					if (expectedEndMinutes <= minutesEnd) {
						// time slot found after last booking
						found = true;
					}

					if (found) {
						result.add(date);
					}
				}
			}

		}
		return result;
	}
}
