package org.bookie.endpoint;

import java.net.URI;
import java.util.Date;

import org.bookie.model.Organization;
import org.bookie.model.Season;
import org.bookie.model.SeasonDetails;
import org.bookie.service.OrganizationService;
import org.bookie.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/season")
public class SeasonEndpoint {

	@Autowired
	private SeasonService seasonService;

	@Autowired
	private OrganizationService organizationService;

	@RequestMapping(method = RequestMethod.POST, value = "/{organizationName}")
	public ResponseEntity<?> createSeason(final String organizationName, @RequestBody final Season season) {
		final Organization org = this.organizationService.findByName(organizationName);
		if (org == null) {
			throw new IllegalStateException("Organization could not be found");
		}
		season.setOrganization(org);
		this.seasonService.createSeason(season);

		final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(season.getId()).toUri();

		final ResponseEntity<Season> response = new ResponseEntity<>(season, HttpStatus.CREATED);
		response.getHeaders().setLocation(location);
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{organizationName}")
	public SeasonDetails getCurrent(final String organizationName) {
		return this.seasonService.getDetailsCurrent(organizationName);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{organizationName}/{date}")
	public SeasonDetails getByDate(@PathVariable final String organizationName, @PathVariable final Date date) {
		return this.seasonService.getDetailsByDate(organizationName, date);
	}
}
