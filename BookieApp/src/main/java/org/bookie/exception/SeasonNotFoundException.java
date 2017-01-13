package org.bookie.exception;

public class SeasonNotFoundException extends NotFoundException {

	public SeasonNotFoundException(final String seasonId) {
		super("Season '" + seasonId + "' could not be found");
	}

}
