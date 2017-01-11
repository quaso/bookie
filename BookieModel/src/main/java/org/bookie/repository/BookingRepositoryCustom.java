package org.bookie.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bookie.model.Booking;
import org.bookie.model.OwnerTimeSlot;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BookingRepositoryCustom extends BookingRepository {

	public List<OwnerTimeSlot> findNoOwner(final String organizationName, final Date timeStart, final Date timeEnd,
			final Collection<String> types, final Collection<String> placeIds, final String ownerId);

	public List<OwnerTimeSlot> findWithOwner(final String organizationName, final Date timeStart, final Date timeEnd,
			final Collection<String> types, final Collection<String> placeIds, final String ownerId);

	public List<OwnerTimeSlot> findBooking(final String organizationName, final Date timeStart, final Date timeEnd,
			final Collection<String> types, final Collection<String> placeIds, final String ownerId);

	public boolean checkFreeTime(Booking booking);
}
