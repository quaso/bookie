package org.bookie.model;

public interface OwnerTimeSlot extends TimeSlot {

	public User getOwner();

	public void setOwner(final User owner);
}
