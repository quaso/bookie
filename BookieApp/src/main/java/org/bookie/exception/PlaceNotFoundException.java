package org.bookie.exception;

public class PlaceNotFoundException extends NotFoundException {

	public PlaceNotFoundException(final String placeId) {
		super("Place '" + placeId + "' could not be found");
	}

}
