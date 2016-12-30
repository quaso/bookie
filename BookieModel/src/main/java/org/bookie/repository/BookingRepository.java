package org.bookie.repository;

import org.bookie.model.Booking;
import org.springframework.data.repository.CrudRepository;

interface BookingRepository extends CrudRepository<Booking, String> {

}
