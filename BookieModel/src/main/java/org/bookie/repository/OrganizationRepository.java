package org.bookie.repository;

import javax.transaction.Transactional;

import org.bookie.model.Organization;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface OrganizationRepository extends CrudRepository<Organization, String> {
	public Organization findByName(String name);
}
