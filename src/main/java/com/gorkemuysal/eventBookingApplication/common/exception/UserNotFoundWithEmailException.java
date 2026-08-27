package com.gorkemuysal.eventBookingApplication.common.exception;

public class UserNotFoundWithEmailException extends RuntimeException {

	public UserNotFoundWithEmailException(String email) {
		super("User not found with this email: " + email);
	}
}
