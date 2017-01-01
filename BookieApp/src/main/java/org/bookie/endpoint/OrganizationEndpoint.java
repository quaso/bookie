package org.bookie.endpoint;

import java.net.URI;

import org.bookie.model.Organization;
import org.bookie.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/organization")
public class OrganizationEndpoint {

	@Autowired
	private OrganizationService organizationService;

	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> createOrganization(@RequestBody final Organization organization) {
		this.organizationService.createOrganization(organization);
		final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(organization.getId()).toUri();

		final ResponseEntity<Organization> response = new ResponseEntity<>(organization, HttpStatus.CREATED);
		response.getHeaders().setLocation(location);
		return response;
	}
}
