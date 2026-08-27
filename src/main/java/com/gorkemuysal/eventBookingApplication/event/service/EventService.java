package com.gorkemuysal.eventBookingApplication.event.service;


import java.util.List;

import com.gorkemuysal.eventBookingApplication.event.dto.EventRequest;
import com.gorkemuysal.eventBookingApplication.event.dto.EventResponse;

import jakarta.validation.Valid;

public interface EventService {
	EventResponse create(@Valid EventRequest request);

	EventResponse getById(Long id);
	
	EventResponse update(@Valid EventRequest request, Long id);
	
	List<EventResponse> getAllEvents();

	EventResponse publish(Long id);
	
	EventResponse cancel(Long id);
}
