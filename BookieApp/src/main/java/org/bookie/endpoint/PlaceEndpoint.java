package org.bookie.endpoint;

import org.bookie.exception.OrganizationNotFoundException;
import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.service.OrganizationService;
import org.bookie.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/place")
public class PlaceEndpoint {

	@Autowired
	private PlaceService placeService;

	@Autowired
	private OrganizationService organizationService;

	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> createPlace(final @RequestHeader String organizationName,
			@RequestBody final Place place) throws OrganizationNotFoundException {
		final Organization org = this.organizationService.findByName(organizationName);
		place.setOrganization(org);
		this.placeService.createOrUpdatePlace(place);
		return new ResponseEntity<>(place, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/")
	public ResponseEntity<?> updateOrganization(final @RequestHeader String organizationName,
			@RequestBody final Place place) throws OrganizationNotFoundException {
		final Organization org = this.organizationService.findByName(organizationName);
		place.setOrganization(org);
		this.placeService.createOrUpdatePlace(place);
		return new ResponseEntity<>(place, HttpStatus.OK);
	}

	//	@RequestMapping(method = RequestMethod.GET, value = "/")
	//	public ResponseEntity<SeasonDetails> getCurrent(final @RequestHeader String organizationName) {
	//		return this.find(() -> this.seasonService.getDetailsCurrent(organizationName));
	//	}

	//	@RequestMapping(method = RequestMethod.GET, value = "/{date}")
	//	public ResponseEntity<SeasonDetails> getByDate(@RequestHeader final String organizationName,
	//			@PathVariable @DateTimeFormat(pattern = "yyyyMMdd") final Date date) {
	//		return this.find(() -> this.seasonService.getDetailsByDate(organizationName, date));
	//	}

	@ExceptionHandler(OrganizationNotFoundException.class)
	public ResponseEntity<Object> handleException(final OrganizationNotFoundException ex) {
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleException(final DataIntegrityViolationException ex) {
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.CONFLICT);
	}
}
