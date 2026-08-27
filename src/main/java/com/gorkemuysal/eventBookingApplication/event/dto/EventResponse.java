package com.gorkemuysal.eventBookingApplication.event.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.gorkemuysal.eventBookingApplication.event.EventStatus;

public record EventResponse(	
		Long id,
		String title,
		String description,
		String vanue,
		LocalDateTime startTime,
		Integer capacity,
		BigDecimal price,
		EventStatus status,
		String createdByUsername) {

}
