package com.gorkemuysal.eventBookingApplication.event.service;


import org.springframework.web.bind.annotation.RequestBody;

import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.event.dto.EventDto;
import com.gorkemuysal.eventBookingApplication.event.dto.EventRequestDto;

import jakarta.validation.Valid;

public interface EventService {
	EventDto create(@Valid @RequestBody EventDto request, Long creatorId);

	EventDto getById(Long id);
	
	EventDto update(@Valid @RequestBody EventDto request, Long id);

}
