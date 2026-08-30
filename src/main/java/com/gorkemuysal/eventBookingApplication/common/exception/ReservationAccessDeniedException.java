package com.gorkemuysal.eventBookingApplication.common.exception;

public class ReservationAccessDeniedException extends RuntimeException{
	 public ReservationAccessDeniedException(Long reservationId) {
	        super("You do not have access to reservation " + reservationId);
	    }
}
