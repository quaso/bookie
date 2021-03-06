package org.bookie.endpoint;

import org.bookie.auth.LoggedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keep-alive")
public class KeepAliveEndpoint {

	@RequestMapping(method = RequestMethod.GET, value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LoggedUser> createOrganization() {
		ResponseEntity<LoggedUser> result;

		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getDetails() == null) {
			result = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} else {
			result = new ResponseEntity<>((LoggedUser) authentication.getDetails(), HttpStatus.OK);
		}

		return result;
	}
}
