package com.gorkemuysal.eventBookingApplication.event.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import com.gorkemuysal.eventBookingApplication.common.exception.EventNotFoundException;
import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.event.EventRepository;
import com.gorkemuysal.eventBookingApplication.event.EventStatus;
import com.gorkemuysal.eventBookingApplication.event.dto.EventDto;
import com.gorkemuysal.eventBookingApplication.event.mapper.EventMapper;
import com.gorkemuysal.eventBookingApplication.event.service.EventService;
import com.gorkemuysal.eventBookingApplication.identity.User;
import com.gorkemuysal.eventBookingApplication.identity.UserRepository;

import jakarta.validation.Valid;

// Event service implentation which implements EventService interface.
@Service
@Validated
public class EventServiceImpl implements EventService {

	private final UserRepository userRepository;
	private final EventMapper eventMapper;
	private final EventRepository eventRepository;

	public EventServiceImpl(UserRepository userRepository, EventMapper eventMapper
			,EventRepository eventRepository ) {
		this.userRepository = userRepository;
		this.eventMapper = eventMapper;
		this.eventRepository = eventRepository;
	}

	/**
	 * Handle creating new Event
	 * 
	 * @param A Request as a Dto object and creatorId as a Long
	 * @return A Dto object as a EventDto instance
	 * */
	@Override
	public EventDto create(@Valid @RequestBody EventDto request, Long creatorId) {
		
		User ref = userRepository.getReferenceById(creatorId);
		
		// mapping with eventMapper interface
		Event event = eventMapper.toEntity(request);
		event.setCreatedBy(ref);
		event.setCapacity(request.capacity());
		event.setStatus(EventStatus.DRAFT);
		
		Event saved = eventRepository.save(event);
		
		return eventMapper.toDto(saved);
	}

	
	/**
	 * Find Event with their names. If event not found throw an exception
	 * 
	 * @param Event id as a Long
	 * @return A dto object which maps by eventMapper
	 * @throws An custom Exception when Event not found with entered id.
	 * */
	@Override
	public EventDto getById(Long id) {
	
		Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		
		return eventMapper.toDto(event);
	}

	
	/**
	 * Handle update events. Throws error when event not found with entered id.
	 * 
	 * @param A Dto object name is request and Event id as a Long
	 * @return A dto object which maps by eventMapper
	 * @throws An custom Exception when Event not found with entered id.
	 * */
	@Override
	public EventDto update(@Valid EventDto request, Long id) {

		Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		
		eventMapper.updateEntityFromDto(request, event);
		Event updated = eventRepository.save(event);
		
		return eventMapper.toDto(updated);
		
	}

}
