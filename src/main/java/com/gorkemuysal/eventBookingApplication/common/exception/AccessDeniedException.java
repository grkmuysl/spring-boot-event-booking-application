package com.gorkemuysal.eventBookingApplication.common.exception;

public class AccessDeniedException extends RuntimeException{

	public AccessDeniedException(String message) {
		super("You do not have permission to do this: " + message);
	}
}
