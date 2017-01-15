package org.bookie.service;

import javax.transaction.Transactional;

import org.bookie.exception.OrganizationNotFoundException;
import org.bookie.model.Organization;
import org.bookie.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrganizationService {

	@Autowired
	private OrganizationRepository organizationRepository;

	public Organization findByCode(final String organizationCode) throws OrganizationNotFoundException {
		return this.organizationRepository.findByCode(organizationCode)
				.orElseThrow(() -> new OrganizationNotFoundException(organizationCode));
	}

	public void createOrUpdateOrganization(final Organization organization) {
		this.organizationRepository.save(organization);
	}

}
