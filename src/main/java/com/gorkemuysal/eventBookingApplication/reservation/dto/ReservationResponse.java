package com.gorkemuysal.eventBookingApplication.reservation.dto;

import java.time.Instant;

import com.gorkemuysal.eventBookingApplication.reservation.ReservationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservationResponse(
		@NotNull Integer seatCount, 
		@NotBlank String createdBy,
		@NotBlank String eventName,
		@NotNull  ReservationStatus status, 
		@NotNull Instant reservedAt, 
		@NotNull Instant expiresAt) {

}
