package org.bookie.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.bookie.model.Booking;
import org.bookie.model.QBooking;
import org.bookie.model.QPlace;
import org.bookie.model.QUser;
import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.core.support.FetchableQueryBase;
import com.querydsl.core.support.QueryBase;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;

public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private BookingRepository bookingRepository;

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Booking> find(final Date dateStart, final Date dateEnd, final Collection<String> types,
			final Collection<String> placeIds, final String ownerId) {

		final QBooking qBooking = QBooking.booking;
		final QPlace qPlace = QPlace.place;
		final QUser qUser = QUser.user;

		JPAQueryBase queryBase = new JPAQuery(this.em).from(qBooking);
		if (placeIds != null) {
			queryBase = queryBase.innerJoin(qBooking.place, qPlace);
		}
		if (ownerId != null) {
			queryBase = queryBase.innerJoin(qBooking.owner, qUser);
		}

		QueryBase query = queryBase.where(qBooking.timeStart.goe(dateStart), qBooking.timeEnd.loe(dateEnd));
		if (types != null) {
			query = query.where(qBooking.type.in(types));
		}
		if (placeIds != null) {
			query = query.where(qPlace.id.in(placeIds));
		}
		if (ownerId != null) {
			query = query.where(qUser.id.eq(ownerId));
		}
		return ((FetchableQueryBase) query).fetch();
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
