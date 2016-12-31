package org.bookie.repository;

import org.bookie.model.Organization;
import org.springframework.data.repository.CrudRepository;

public interface OrganizationRepository extends CrudRepository<Organization, String> {
	public Organization findByName(String name);
}
