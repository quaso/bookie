package org.bookie.endpoint;

import java.util.Date;
import java.util.function.Supplier;

import org.bookie.model.Organization;
import org.bookie.model.Season;
import org.bookie.model.SeasonDetails;
import org.bookie.service.OrganizationService;
import org.bookie.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/season")
public class SeasonEndpoint {

	@Autowired
	private SeasonService seasonService;

	@Autowired
	private OrganizationService organizationService;

	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> createSeason(final @RequestHeader String organizationName,
			@RequestBody final Season season) {
		final Organization org = this.organizationService.findByName(organizationName);
		if (org == null) {
			throw new IllegalStateException("Organization could not be found");
		}
		season.setOrganization(org);
		this.seasonService.createSeason(season);
		return new ResponseEntity<>(season, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public ResponseEntity<SeasonDetails> getCurrent(final @RequestHeader String organizationName) {
		return this.find(() -> this.seasonService.getDetailsCurrent(organizationName));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{date}")
	public ResponseEntity<SeasonDetails> getByDate(@RequestHeader final String organizationName,
			@PathVariable @DateTimeFormat(pattern = "yyyyMMdd") final Date date) {
		return this.find(() -> this.seasonService.getDetailsByDate(organizationName, date));
	}

	private ResponseEntity<SeasonDetails> find(final Supplier<SeasonDetails> supplier) {
		final SeasonDetails season = supplier.get();
		ResponseEntity<SeasonDetails> result;
		if (season == null) {
			result = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			result = new ResponseEntity<SeasonDetails>(season, HttpStatus.OK);
		}
		return result;
	}
}
