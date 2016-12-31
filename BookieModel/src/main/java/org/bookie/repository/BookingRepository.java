package org.bookie.repository;

import org.bookie.model.Booking;
import org.springframework.data.repository.CrudRepository;

interface BookingRepository extends CrudRepository<Booking, String> {
	// public Long
	// countByTimeStartGreaterThanEqualAndTimeEndLessThanEqualAndPlaceIdEquals(final
	// Date timeStart,
	// final Date timeEnd, String placeId);
}
