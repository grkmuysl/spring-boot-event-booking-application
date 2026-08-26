package com.gorkemuysal.eventBookingApplication.event.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.event.dto.EventDto;
import com.gorkemuysal.eventBookingApplication.event.dto.EventRequestDto;
import com.gorkemuysal.eventBookingApplication.event.service.EventService;

import jakarta.validation.Valid;

@Service
@Validated
public class EventServiceImpl implements EventService {
	
	
	
	@Override
	public EventDto create(@Valid @RequestBody EventRequestDto request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventDto getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public EventDto toDto(Event event) {
		 
		return new EventDto(
				event.getTitle(),
				event.getDescription(), 
				event.getVanue(), 
				event.getStartTime(), 
				event.getCapacity(), 
				event.getPrice(),
				event.getStatus(), 
				event.getCreatedBy()
				);
	}

}
