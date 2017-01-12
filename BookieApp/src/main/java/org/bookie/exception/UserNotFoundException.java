package org.bookie.exception;

import org.apache.commons.lang3.StringUtils;

public class UserNotFoundException extends Exception {

	public UserNotFoundException(final String id, final String username) {
		super(createMessage(id, username));
	}

	private static String createMessage(final String id, final String username) {
		String result = "User not found";
		if (StringUtils.isEmpty(id)) {
			result = "User with id " + id + " not found";
		} else if (StringUtils.isEmpty(username)) {
			result = "Username " + username + " not found";
		}
		return result;
	}

}
