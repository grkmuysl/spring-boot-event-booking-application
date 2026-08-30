package com.gorkemuysal.eventBookingApplication.common.exception;

import com.gorkemuysal.eventBookingApplication.reservation.ReservationStatus;

public class InvalidReservationStateException extends RuntimeException {
    public InvalidReservationStateException(Long id, ReservationStatus current) {
        super("Reservation " + id + " cannot be confirmed, current status: " + current);
    }
}