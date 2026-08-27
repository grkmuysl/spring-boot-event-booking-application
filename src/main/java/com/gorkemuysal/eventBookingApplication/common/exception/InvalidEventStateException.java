package com.gorkemuysal.eventBookingApplication.common.exception;

public class InvalidEventStateException extends RuntimeException{
	public InvalidEventStateException(String message) {
		super(message);
	}
}
