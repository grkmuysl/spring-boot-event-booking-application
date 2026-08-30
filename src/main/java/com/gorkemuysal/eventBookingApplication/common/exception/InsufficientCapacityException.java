package com.gorkemuysal.eventBookingApplication.common.exception;

public class InsufficientCapacityException extends RuntimeException {
	 public InsufficientCapacityException(Long eventId, int requested, int available) {
	        super("Event " + eventId + " has only " + available + " seats available, requested " + requested);
	    }
}
