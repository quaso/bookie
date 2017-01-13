package org.bookie.exception;

public abstract class NotFoundException extends Exception {

	public NotFoundException(final String message) {
		super(message);
	}

}
