package org.bookie.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.bookie.auth.LoggedUser;
import org.bookie.exception.NotFreeException;
import org.bookie.model.Booking;
import org.bookie.model.BookingPattern;
import org.bookie.model.OwnerTimeSlot;
import org.bookie.model.Place;
import org.bookie.model.Season;
import org.bookie.model.TimeSlot;
import org.bookie.model.User;
import org.bookie.repository.BookingRepositoryCustom;
import org.bookie.repository.PlaceRepository;
import org.bookie.util.BookingPatternIterator;
import org.bookie.util.BookingPatternIterator.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BookingService {

	@Autowired
	private BookingRepositoryCustom bookingRepository;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private SeasonService seasonService;

	public List<Booking> createBooking(final BookingPattern bookingPattern, final String type,
			final String ownerId, final String placeId, final String note) throws NotFreeException {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			throw new IllegalStateException("Only authenicated users may call this");
		}
		final User createdBy = this.extractDbUserFromAuth(authentication);

		final User owner = this.userService.findById(ownerId);
		if (owner == null) {
			throw new IllegalStateException("Owner could not be identified");
		}

		final Place place = this.placeRepository.findOne(placeId);
		if (place == null) {
			throw new IllegalStateException("Place could not be identified");
		}

		final Season season = this.seasonService.getByDate(place.getOrganization().getCode(),
				bookingPattern.getStartDate());
		if (season == null) {
			throw new IllegalStateException("No season is defined for requested booking date");
		}

		final CreateBookingParameters params = new CreateBookingParameters(season, owner, place, type, note, createdBy);

		final List<Booking> result = new ArrayList<>();
		if (bookingPattern.isReccurent()) {
			result.addAll(this.createBookings(bookingPattern, params));
		} else {
			final LocalDateTime timeStart = bookingPattern.getStartDate().atTime(bookingPattern.getTimeStart());
			final LocalDateTime timeEnd = bookingPattern.getEndDate().atTime(bookingPattern.getTimeEnd());

			result.add(this.createSingleBooking(timeStart, timeEnd, params));
		}

		this.bookingRepository.save(result);
		return result;
	}

	private Booking createSingleBooking(final LocalDateTime timeStart, final LocalDateTime timeEnd,
			final CreateBookingParameters parameters) throws NotFreeException {

		if (timeStart.isAfter(timeEnd)) {
			throw new IllegalArgumentException("Start must be before end");
		}
		if (!this.checkSameDay(timeStart, timeEnd)) {
			throw new IllegalStateException("Start and end dates must be in the same day");
		}

		final Booking booking = new Booking();
		booking.setTimeStart(Date.from(timeStart.atZone(ZoneId.systemDefault()).toInstant()));
		booking.setTimeEnd(Date.from(timeEnd.atZone(ZoneId.systemDefault()).toInstant()));
		booking.setSeason(parameters.season);
		booking.setOwner(parameters.owner);
		booking.setPlace(parameters.place);
		booking.setCreatedAt(new Date());
		booking.setCreatedBy(parameters.createdBy);
		booking.setType(parameters.type);
		booking.setNote(parameters.note);

		// check if the datetime is free
		if (!this.bookingRepository.checkFreeTime(booking)) {
			throw new NotFreeException(timeStart, timeEnd, parameters.place);
		}
		return booking;
	}

	private Collection<Booking> createBookings(final BookingPattern pattern, final CreateBookingParameters parameters)
			throws NotFreeException {
		final List<NotFreeException> exceptions = new ArrayList<>();
		final Collection<Booking> result = new ArrayList<>();

		final BookingPatternIterator patternIterator = new BookingPatternIterator(pattern, parameters.season);
		while (patternIterator.hasNext()) {
			final Interval next = patternIterator.next();
			try {
				result.add(this.createSingleBooking(next.getStart(), next.getEnd(), parameters));
			} catch (final NotFreeException e) {
				exceptions.add(e);
				// premature iteration finish - too many exceptions
				if (exceptions.size() == 5) {
					throw new NotFreeException(exceptions);
				}
			}
		}

		if (exceptions.size() > 0) {
			throw new NotFreeException(exceptions);
		}

		return result;
	}

	public void delete(final String bookingId) {
		this.bookingRepository.delete(bookingId);
		//TODO: check and send email to the others about vacant time
	}

	public List<? extends OwnerTimeSlot> find(final String organizationCode, final Date timeStart, final Date timeEnd,
			final Collection<String> types, final Collection<String> placeIds, final String ownerId) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean admin = false;
		if (authentication != null) {
			admin = authentication.getAuthorities().stream()
					.anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN")
							|| ga.getAuthority().equals("ROLE_SUPER_ADMIN"));
		}
		final List<OwnerTimeSlot> list;
		if (admin) {
			list = this.bookingRepository.findBooking(organizationCode, timeStart, timeEnd,
					types, placeIds, ownerId);
		} else {
			list = this.bookingRepository.findWithOwner(organizationCode, timeStart, timeEnd,
					types, placeIds, ownerId);
		}

		if (!admin) {
			// remove user info for other owners
			final String userId = authentication != null ? this.extractDbUserFromAuth(authentication).getId() : null;
			list.forEach(ots -> {
				if (!ots.getOwner().getId().equals(userId)) {
					ots.setOwner(null);
				}
			});
		}
		return list;
	}

	public Set<LocalDate> findFreeTimeSlots(final String organizationCode, final int duration, final int minutesStart,
			final int minutesEnd, final Collection<LocalDate> days) {
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
			final List<OwnerTimeSlot> timeSlots = this.bookingRepository.findNoOwner(organizationCode, start, end, null,
					null, null);

			if (timeSlots.isEmpty()) {
				// there are no booking at this day
				result.add(date);
			} else {
				// find unique placeIds
				final List<String> placeIds = timeSlots.stream().map(b -> b.getPlace().getId()).distinct()
						.collect(Collectors.toList());

				// for each placeId evaluate if there is free time slot (or until
				// first match)
				for (final String pid : placeIds) {
					// get bookings just for the placeId
					final List<TimeSlot> timeSlotsForPlace = timeSlots.stream()
							.filter(b -> pid.equals(b.getPlace().getId()))
							.collect(Collectors.toList());

					boolean found = false;
					LocalDateTime timeEnd = date.atStartOfDay().plusMinutes(minutesStart);
					for (final TimeSlot timeSlot : timeSlotsForPlace) {
						timeEnd = timeEnd.plusMinutes(duration);
						final int expectedEndMinutes = timeEnd.getHour() * 60 + timeEnd.getMinute();
						if (expectedEndMinutes > minutesEnd || expectedEndMinutes > 24 * 60) {
							// expected end is after desired end or after midnight
							// this method has no test coverage although there is a test case for it :) Thanks to JVM optimization :(
							break;
						}
						final LocalDateTime timeStart = LocalDateTime.ofInstant(timeSlot.getTimeStart().toInstant(),
								ZoneId.systemDefault());
						if (timeStart.compareTo(timeEnd) >= 0) {
							// time slot found
							found = true;
							break;
						}
						// get end of the booking
						timeEnd = LocalDateTime.ofInstant(timeSlot.getTimeEnd().toInstant(), ZoneId.systemDefault());
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

	private boolean checkSameDay(final LocalDateTime timeStart, final LocalDateTime timeEnd) {
		final LocalDate lds = timeStart.toLocalDate();
		final LocalDate lde = timeEnd.toLocalDate();
		return lds.equals(lde);
	}

	private User extractDbUserFromAuth(final Authentication authentication) {
		return ((LoggedUser) authentication.getPrincipal()).getDbUser();
	}

	private class CreateBookingParameters {

		final Season season;
		final User owner;
		final Place place;
		final String type;
		final String note;
		final User createdBy;

		public CreateBookingParameters(final Season season, final User owner, final Place place, final String type,
				final String note, final User createdBy) {
			this.season = season;
			this.owner = owner;
			this.place = place;
			this.type = type;
			this.note = note;
			this.createdBy = createdBy;
		}

	}
}
