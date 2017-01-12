package org.bookie.repository;

import javax.transaction.Transactional;

import org.bookie.model.Place;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface PlaceRepository extends CrudRepository<Place, String> {
}
