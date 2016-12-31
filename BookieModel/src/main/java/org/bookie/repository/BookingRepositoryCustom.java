package org.bookie.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bookie.model.Booking;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BookingRepositoryCustom extends BookingRepository {

	public List<Booking> find(final Date dateStart, final Date dateEnd, final Collection<String> types,
			final Collection<String> placeIds, final String ownerId);

	public boolean checkFreeTime(Booking booking);
}
