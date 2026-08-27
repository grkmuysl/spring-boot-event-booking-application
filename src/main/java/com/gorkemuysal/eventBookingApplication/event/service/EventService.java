package com.gorkemuysal.eventBookingApplication.event.service;


import org.springframework.web.bind.annotation.RequestBody;

import com.gorkemuysal.eventBookingApplication.event.dto.EventRequest;
import com.gorkemuysal.eventBookingApplication.event.dto.EventResponse;

import jakarta.validation.Valid;

public interface EventService {
	EventResponse create(@Valid @RequestBody EventRequest request, Long creatorId);

	EventResponse getById(Long id);
	
	EventResponse update(@Valid @RequestBody EventRequest request, Long id);

}
