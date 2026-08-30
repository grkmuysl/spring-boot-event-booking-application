package com.gorkemuysal.eventBookingApplication.common.exception;

public class EventNotAvailableException extends RuntimeException{

	public EventNotAvailableException(Long eventId) {
        super("Event with id " + eventId + " is not available for reservation");
    } 
}
