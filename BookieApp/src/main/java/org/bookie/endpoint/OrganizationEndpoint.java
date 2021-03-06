package org.bookie.endpoint;

import org.bookie.model.Organization;
import org.bookie.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organization")
public class OrganizationEndpoint {

	@Autowired
	private OrganizationService organizationService;

	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> createOrganization(@RequestBody final Organization organization) {
		this.organizationService.createOrUpdateOrganization(organization);
		return new ResponseEntity<>(organization, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/")
	public ResponseEntity<?> updateOrganization(@RequestBody final Organization organization) {
		this.organizationService.createOrUpdateOrganization(organization);
		return new ResponseEntity<>(organization, HttpStatus.OK);
	}
}
