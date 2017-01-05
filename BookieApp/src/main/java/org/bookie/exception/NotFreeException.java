package org.bookie.exception;

import java.util.Date;

import org.bookie.model.Place;

public class NotFreeException extends Exception {

	public NotFreeException(final Date start, final Date end, final Place place) {
		super("Time slot " + start + "-" + end + "is not free for place " + place.getId());
	}

}
