package org.bookie.endpoint;

import java.net.URI;
import java.util.Date;

import org.bookie.model.Season;
import org.bookie.model.SeasonDetails;
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

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createSeason(@RequestBody final Season season) {
		this.seasonService.createSeason(season);

		final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(season.getId()).toUri();

		final ResponseEntity<Season> response = new ResponseEntity<>(season, HttpStatus.CREATED);
		response.getHeaders().setLocation(location);
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/")
	public SeasonDetails getCurrent() {
		return this.seasonService.getDetailsCurrent();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{date}")
	public SeasonDetails getByDate(@PathVariable final Date date) {
		return this.seasonService.getDetailsByDate(date);
	}
}
