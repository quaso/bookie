package org.bookie.endpoint;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import org.bookie.exception.NotFreeException;
import org.bookie.model.Booking;
import org.bookie.model.BookingEx;
import org.bookie.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
public class BookingEndpoint {

	@Autowired
	private BookingService bookingService;

	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> createBooking(final @RequestHeader String organizationCode, //TODO----------organizationCode is not used
			final @RequestBody BookingEx booking) throws NotFreeException {
		final Collection<Booking> result = this.bookingService.createBooking(booking.getPattern(), booking.getType(),
				booking.getOwnerId(), booking.getPlaceId(), booking.getNote());

		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/findFree/")
	public Set<LocalDate> findFreeTimeSlots(final @RequestHeader String organizationCode,
			@RequestParam final int duration, @RequestParam final int timeStart, @RequestParam final int timeEnd,
			@RequestParam final Set<LocalDate> days) {
		return this.bookingService.findFreeTimeSlots(organizationCode, duration, timeStart, timeEnd, days);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/")
	@ResponseStatus(HttpStatus.OK)
	public void deleteBooking(final String bookingId) {
		this.bookingService.delete(bookingId);
	}

	@ExceptionHandler(NotFreeException.class)
	public ResponseEntity<Object> handleException(final NotFreeException ex) {
		return new ResponseEntity<Object>("Requested time slot is not free", HttpStatus.CONFLICT);
	}
}
