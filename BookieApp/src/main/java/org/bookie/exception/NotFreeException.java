package org.bookie.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bookie.model.Place;

public class NotFreeException extends Exception {

	public NotFreeException(final LocalDateTime start, final LocalDateTime end, final Place place) {
		super("Time slot " + start + "-" + end + "is not free for place " + place.getId());
	}

	public NotFreeException(final List<NotFreeException> exceptions) {
		super("Multiple times are not free:\n" + StringUtils.join(exceptions, "\n"));
	}

}
