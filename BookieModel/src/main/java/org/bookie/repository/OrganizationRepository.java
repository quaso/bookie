package org.bookie.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.bookie.model.Organization;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface OrganizationRepository extends CrudRepository<Organization, String> {
	public Optional<Organization> findByName(String name);
}
