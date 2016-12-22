package org.bookie.repository;

import org.bookie.model.Place;
import org.springframework.data.repository.CrudRepository;

public interface PlaceRepository extends CrudRepository<Place, String> {
}
