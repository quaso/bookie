package org.bookie.service;

import java.time.LocalDate;
import java.util.Date;

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

		final Season season = this.seasonService.getByDate(timeStart);
		if (season == null) {
			throw new IllegalStateException("No season is defined for requested booking date");
		}

		final Place place = this.placeRepository.findOne(placeId);
		if (place == null) {
			throw new IllegalStateException("Place could not be identified");
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
}
