package org.bookie.endpoint;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bookie.exception.NotFoundException;
import org.bookie.exception.UserContactsBlankException;
import org.bookie.exception.UserNotFoundException;
import org.bookie.model.User;
import org.bookie.service.UserService;
import org.bookie.util.password.PasswordPolicyException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserEndpoint {

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.POST, value = "/")
	public ResponseEntity<?> createUser(final @RequestBody User user) throws PasswordPolicyException {
		this.userService.createUser(user);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/password/")
	@ResponseStatus(HttpStatus.CREATED)
	public void generatePassword(final @RequestParam String username) throws UserNotFoundException {
		this.userService.generatePassword(username);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/")
	public ResponseEntity<?> update(final @RequestBody User user) throws UserNotFoundException {
		this.userService.updateUser(user);
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/")
	@ResponseStatus(HttpStatus.OK)
	public void disableUser(final String username) throws UserNotFoundException {
		this.userService.disableUser(username);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/role/")
	@ResponseStatus(HttpStatus.CREATED)
	public void addUserToRole(final @RequestHeader String organizationName,
			final @RequestParam String userId, final @RequestParam String roleName) throws NotFoundException {
		this.userService.addUserToRole(userId, roleName, organizationName);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/role/")
	@ResponseStatus(HttpStatus.OK)
	public void removeUserFromRole(final @RequestHeader String organizationName,
			final @RequestParam String userId, final @RequestParam String roleName) {
		this.userService.deleteUserFromRole(userId, roleName, organizationName);
	}

	@ExceptionHandler({ EmptyResultDataAccessException.class, NotFoundException.class })
	public ResponseEntity<Object> handleNotFoundException(final Exception ex) {
		return new ResponseEntity<Object>(ExceptionUtils.getRootCauseMessage(ex), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler({ PasswordPolicyException.class, UserContactsBlankException.class })
	public ResponseEntity<Object> handlePreConditionException(final Exception ex) {
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.PRECONDITION_FAILED);
	}
}
