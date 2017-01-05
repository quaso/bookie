package org.bookie.model;

import java.util.Date;

public interface TimeSlot {
	public Date getTimeStart();

	public Date getTimeEnd();

	public Place getPlace();
}
