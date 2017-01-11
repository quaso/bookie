package org.bookie.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.bookie.model.Booking;
import org.bookie.model.OwnerTimeSlot;
import org.bookie.model.QBooking;
import org.bookie.model.QOrganization;
import org.bookie.model.QPlace;
import org.bookie.model.QUser;
import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.core.support.FetchableQueryBase;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;

public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private BookingRepository bookingRepository;

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<OwnerTimeSlot> findNoOwner(final String organizationName, final Date timeStart, final Date timeEnd,
			final Collection<String> types, final Collection<String> placeIds, final String ownerId) {
		return this.find(organizationName, timeStart, timeEnd, types, placeIds, ownerId, queryBase -> {
			return (JPAQueryBase) queryBase.select(
					Projections.fields(Booking.class, QBooking.booking.timeStart, QBooking.booking.timeEnd,
							QBooking.booking.place));
		});
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<OwnerTimeSlot> findWithOwner(final String organizationName, final Date timeStart, final Date timeEnd,
			final Collection<String> types, final Collection<String> placeIds, final String ownerId) {

		return this.find(organizationName, timeStart, timeEnd, types, placeIds, ownerId, queryBase -> {
			return (JPAQueryBase) queryBase.select(
					Projections.fields(Booking.class, QBooking.booking.timeStart, QBooking.booking.timeEnd,
							QBooking.booking.place, QBooking.booking.owner));
		});
	}

	@Override
	public List<OwnerTimeSlot> findBooking(final String organizationName, final Date timeStart, final Date timeEnd,
			final Collection<String> types, final Collection<String> placeIds, final String ownerId) {
		return this.find(organizationName, timeStart, timeEnd, types, placeIds, ownerId, queryBase -> {
			return queryBase;
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<OwnerTimeSlot> find(final String organizationName, final Date timeStart, final Date timeEnd,
			final Collection<String> types, final Collection<String> placeIds, final String ownerId,
			final Function<JPAQueryBase, JPAQueryBase> selectGenerator) {

		final QBooking qBooking = QBooking.booking;
		final QOrganization qOrganization = QOrganization.organization;
		final QPlace qPlace = QPlace.place;
		final QUser qUser = QUser.user;

		JPAQueryBase queryBase = new JPAQuery(this.em);

		queryBase = selectGenerator.apply(queryBase);

		queryBase = queryBase.from(qBooking);
		if (placeIds != null) {
			queryBase = queryBase.innerJoin(qBooking.place, qPlace);
		} else {
			queryBase = queryBase.innerJoin(qBooking.place.organization, qOrganization);
		}
		if (ownerId != null) {
			queryBase = queryBase.innerJoin(qBooking.owner, qUser);
		}

		// other start is during booking
		final BooleanExpression timeStartPredicate = qBooking.timeStart.gt(timeStart)
				.and(qBooking.timeStart.lt(timeEnd));
		// other end is during booking
		final BooleanExpression timeEndPredicate = qBooking.timeEnd.gt(timeStart).and(qBooking.timeEnd.lt(timeEnd));
		// other start is before and other end is after booking
		final BooleanExpression wholePredicate = qBooking.timeStart.loe(timeStart).and(qBooking.timeEnd.goe(timeEnd));

		QueryBase query = queryBase.where(timeStartPredicate.or(timeEndPredicate).or(wholePredicate));
		if (types != null) {
			query = query.where(qBooking.type.in(types));
		}
		if (placeIds != null) {
			query = query.where(qPlace.id.in(placeIds));
		} else {
			query = query.where(qOrganization.name.eq(organizationName));
		}
		if (ownerId != null) {
			query = query.where(qUser.id.eq(ownerId));
		}
		return ((FetchableQueryBase) query).fetch();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean checkFreeTime(final Booking booking) {
		final QBooking qBooking = QBooking.booking;
		final QPlace qPlace = QPlace.place;

		final JPAQueryBase queryBase = new JPAQuery(this.em).from(qBooking).innerJoin(qBooking.place, qPlace);

		// other start is during booking
		final BooleanExpression timeStartPredicate = qBooking.timeStart.gt(booking.getTimeStart())
				.and(qBooking.timeStart.lt(booking.getTimeEnd()));
		// other end is during booking
		final BooleanExpression timeEndPredicate = qBooking.timeEnd.gt(booking.getTimeStart())
				.and(qBooking.timeEnd.lt(booking.getTimeEnd()));
		// other start is before and other end is after booking
		final BooleanExpression wholePredicate = qBooking.timeStart.loe(booking.getTimeStart())
				.and(qBooking.timeEnd.goe(booking.getTimeEnd()));

		final JPAQuery query = (JPAQuery) queryBase.where(qPlace.id.in(booking.getPlace().getId()))
				.where(timeStartPredicate.or(timeEndPredicate).or(wholePredicate));
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);

		return query.fetchCount() == 0;
	}

	@Override
	public <S extends Booking> S save(final S entity) {
		return this.bookingRepository.save(entity);
	}

	@Override
	public <S extends Booking> Iterable<S> save(final Iterable<S> entities) {
		return this.bookingRepository.save(entities);
	}

	@Override
	public Booking findOne(final String id) {
		return this.bookingRepository.findOne(id);
	}

	@Override
	public boolean exists(final String id) {
		return this.bookingRepository.exists(id);
	}

	@Override
	public Iterable<Booking> findAll() {
		return this.bookingRepository.findAll();
	}

	@Override
	public Iterable<Booking> findAll(final Iterable<String> ids) {
		return this.bookingRepository.findAll(ids);
	}

	@Override
	public long count() {
		return this.bookingRepository.count();
	}

	@Override
	public void delete(final String id) {
		this.bookingRepository.delete(id);
	}

	@Override
	public void delete(final Booking entity) {
		this.bookingRepository.delete(entity);
	}

	@Override
	public void delete(final Iterable<? extends Booking> entities) {
		this.bookingRepository.delete(entities);
	}

	@Override
	public void deleteAll() {
		this.bookingRepository.deleteAll();
	}
}
