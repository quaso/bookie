package org.bookie.endpoint;

import java.util.Date;
import java.util.function.Supplier;

import org.bookie.exception.NotFoundException;
import org.bookie.exception.OrganizationNotFoundException;
import org.bookie.model.Organization;
import org.bookie.model.Season;
import org.bookie.model.SeasonDetails;
import org.bookie.service.OrganizationService;
import org.bookie.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
			@RequestBody final Season season) throws OrganizationNotFoundException {
		final Organization org = this.organizationService.findByName(organizationName);
		season.setOrganization(org);
		this.seasonService.createOrUpdateSeason(season);
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

	@RequestMapping(method = RequestMethod.POST, value = "/place/")
	@ResponseStatus(HttpStatus.CREATED)
	public void assignPlaceToSeason(@RequestParam final String seasonId, @RequestParam final String placeId)
			throws NotFoundException {
		this.seasonService.assignPlaceToSeason(placeId, seasonId);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/place/")
	@ResponseStatus(HttpStatus.CREATED)
	public void unassignPlaceToSeason(@RequestParam final String seasonId, @RequestParam final String placeId) {
		this.seasonService.unassignPlaceFromSeason(placeId, seasonId);
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

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Object> handleException(final OrganizationNotFoundException ex) {
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleException(final DataIntegrityViolationException ex) {
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.CONFLICT);
	}
}
