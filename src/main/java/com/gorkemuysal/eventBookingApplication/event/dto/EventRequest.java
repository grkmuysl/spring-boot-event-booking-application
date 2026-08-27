package com.gorkemuysal.eventBookingApplication.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.gorkemuysal.eventBookingApplication.event.EventStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EventRequest(
		@NotBlank String title,
		@NotBlank String description,
		@NotBlank String vanue,
		@NotNull LocalDateTime startTime,
		@NotNull @Positive Integer capacity,
		@Positive @NotNull BigDecimal price,
		@NotNull EventStatus status
		) {

}
