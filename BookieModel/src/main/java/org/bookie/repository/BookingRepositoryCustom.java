package org.bookie.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bookie.model.Booking;
import org.bookie.model.TimeSlot;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BookingRepositoryCustom extends BookingRepository {

	public <T extends TimeSlot> List<T> find(final String organizationName, final Date timeStart, final Date timeEnd,
			final Collection<String> types, final Collection<String> placeIds, final String ownerId,
			final Class<T> clazz);

	public boolean checkFreeTime(Booking booking);
}
