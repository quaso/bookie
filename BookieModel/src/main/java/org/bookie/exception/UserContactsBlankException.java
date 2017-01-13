package org.bookie.exception;

public class UserContactsBlankException extends RuntimeException {

	public UserContactsBlankException() {
		super("User contact details are missing");
	}

}
