package org.bookie.endpoint;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bookie.exception.UserNotFoundException;
import org.bookie.model.User;
import org.bookie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserEndpoint {

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> createUser(final @RequestHeader String organizationName, final @RequestBody User user) {
		this.userService.createUser(user);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/")
	public ResponseEntity<?> update(final @RequestHeader String organizationName, final @RequestBody User user)
			throws UserNotFoundException {
		this.userService.updateUser(user);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/")
	@ResponseStatus(HttpStatus.OK)
	public void disableUser(final String username) throws UserNotFoundException {
		this.userService.disableUser(username);
	}

	@ExceptionHandler(EmptyResultDataAccessException.class)
	public ResponseEntity<Object> handleNotFoundException(final Exception ex) {
		return new ResponseEntity<Object>(ExceptionUtils.getRootCauseMessage(ex), new HttpHeaders(),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleNotFoundException(final UserNotFoundException ex) {
		return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
	}
}
