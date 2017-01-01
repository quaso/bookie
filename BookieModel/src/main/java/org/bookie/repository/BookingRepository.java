package org.bookie.repository;

import java.util.Date;
import java.util.List;

import org.bookie.model.Booking;
import org.springframework.data.repository.CrudRepository;

interface BookingRepository extends CrudRepository<Booking, String> {

	public List<Booking> findTimeStartGreaterThanEqualAndTimeEndLessThanEqualOrderByTimeStart(Date timeStart,
			Date timeEnd);
}
