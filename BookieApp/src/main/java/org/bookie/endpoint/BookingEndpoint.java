package org.bookie.endpoint;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bookie.exception.NotFreeException;
import org.bookie.model.Booking;
import org.bookie.model.BookingEx;
import org.bookie.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/booking")
public class BookingEndpoint {

	@Autowired
	private BookingService bookingService;

	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> createBooking(final BookingEx booking) throws NotFreeException {
		final Booking result = this.bookingService.createBooking(booking.getTimeStart(), booking.getTimeEnd(),
				booking.getType(), booking.getOwnerId(), booking.getPlaceId(), booking.getNote());

		final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(result.getId()).toUri();

		final ResponseEntity<Booking> response = new ResponseEntity<>(result, HttpStatus.CREATED);
		response.getHeaders().setLocation(location);
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/findFree/")
	public Set<LocalDate> findFreeTimeSlots(@RequestParam final int duration, @RequestParam final int timeStart,
			@RequestParam final int timeEnd, @RequestParam final Set<LocalDate> days) {
		return this.bookingService.findFreeTimeSlots(duration, timeStart, timeEnd, days);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/")
	@ResponseStatus(HttpStatus.OK)
	public void deleteBooking(final String bookingId) {
		this.bookingService.delete(bookingId);
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<Object> handleNotFoundException(final Exception ex) {
		return new ResponseEntity<Object>(ExceptionUtils.getRootCauseMessage(ex), new HttpHeaders(),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(NotFreeException.class)
	public ResponseEntity<Object> handleNotFoundException(final NotFreeException ex) {
		return new ResponseEntity<Object>("Requested time slot is not free", new HttpHeaders(), HttpStatus.CONFLICT);
	}
}
