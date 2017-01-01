package org.bookie.service;

import org.bookie.model.Organization;
import org.bookie.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository organizationRepository;

	public Organization findByName(final String organizationName) {
		return this.organizationRepository.findByName(organizationName);
	}

	public void createOrganization(final Organization organization) {
		this.organizationRepository.save(organization);
	}

}
