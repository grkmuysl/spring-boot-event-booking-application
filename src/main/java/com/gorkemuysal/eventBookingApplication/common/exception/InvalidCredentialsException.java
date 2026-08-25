package com.gorkemuysal.eventBookingApplication.common.exception;

//Intentionally has a single generic message, used for both
//"email not found" and "wrong password" cases, to avoid
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid email or password.");
    }
}
