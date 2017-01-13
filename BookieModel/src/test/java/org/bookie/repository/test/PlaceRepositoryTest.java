package org.bookie.repository.test;

import java.util.Iterator;

import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class PlaceRepositoryTest extends AbstractTest {

	@Test
	public void testCreate() {
		final Organization org = this.createOrganization("name");
		final Place place = new Place();
		place.setName("place1");
		place.setType("type1");
		place.setOrganization(org);
		this.placeRepository.save(place);
		Assert.assertNotNull(place.getId());

		final Iterator<Place> dbPlaceIterator = this.placeRepository.findAll().iterator();
		Assert.assertTrue(dbPlaceIterator.hasNext());
		final Place dbPlace = dbPlaceIterator.next();
		Assert.assertEquals(place.getId(), dbPlace.getId());
		Assert.assertFalse(dbPlaceIterator.hasNext());
	}

	@Test
	public void testCreateUnique() {
		final Organization org = this.createOrganization("name");
		final Place place = new Place();
		place.setName("place1");
		place.setType("type1");
		place.setOrganization(org);
		this.placeRepository.save(place);

		this.placeRepository.findAll();
		try {
			final Place place2 = new Place();
			place2.setName("place1");
			place2.setType("type1");
			place2.setOrganization(org);
			this.placeRepository.save(place2);
			this.placeRepository.findAll();
			Assert.fail("Unique constraint for name/type/organization failed");
		} catch (final DataIntegrityViolationException ex) {
		}
	}
}
