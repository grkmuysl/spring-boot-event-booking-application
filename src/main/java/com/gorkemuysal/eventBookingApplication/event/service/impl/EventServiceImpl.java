package com.gorkemuysal.eventBookingApplication.event.service.impl;
import static com.gorkemuysal.eventBookingApplication.config.CacheConfig.EVENTS_CACHE;
import static com.gorkemuysal.eventBookingApplication.config.CacheConfig.ALL_EVENTS_CACHE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.gorkemuysal.eventBookingApplication.common.exception.AccessDeniedException;
import com.gorkemuysal.eventBookingApplication.common.exception.EventNotFoundException;
import com.gorkemuysal.eventBookingApplication.common.exception.InvalidEventStateException;
import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.event.EventRepository;
import com.gorkemuysal.eventBookingApplication.event.EventStatus;
import com.gorkemuysal.eventBookingApplication.event.dto.EventRequest;
import com.gorkemuysal.eventBookingApplication.event.dto.EventResponse;
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
	 * The CacheEvict annotaions is used in data situations that are no longer valid
	 * 
	 * @param A Request as a Dto object and creatorId as a Long
	 * @return A Dto object as a EventDto instance
	 * */
	@Override
	@CacheEvict(value = ALL_EVENTS_CACHE, allEntries = true)
	public EventResponse create(@Valid EventRequest request) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();
		
		User currentUser = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));
		
		Event event = eventMapper.toEntity(request);
		event.setCreatedBy(currentUser);
		event.setStatus(request.status());
		event.setAvailableSeats(request.capacity());
		
		Event saved = eventRepository.save(event);
		
		return eventMapper.toResponse(saved);
	}

	
	/**
	 * Find Event with their names. If event not found throw an exception
	 * 
	 * The Cacheable annotaion is used in read operations.
	 * 
	 * @param Event id as a Long
	 * @return A dto object which maps by eventMapper
	 * @throws An custom Exception when Event not found with entered id.
	 * */
	@Override
	@Cacheable(value = EVENTS_CACHE, key = "#id")
	public EventResponse getById(Long id) {
	
		Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		
		return eventMapper.toResponse(event);
	}

	
	/**
	 * Handle update events. Throws error when event not found with entered id.
	 * 
	 * The CachePut annotaions is used in update oprations
	 * 
	 * @param A Dto object name is request and Event id as a Long
	 * @return A dto object which maps by eventMapper
	 * @throws An custom Exception when Event not found with entered id.
	 * */
	@Override
	@CachePut(value = EVENTS_CACHE, key = "#id")
    @CacheEvict(value = ALL_EVENTS_CACHE, allEntries = true)
	public EventResponse update(@Valid EventRequest request, Long id) {

		Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		
		checkOwnership(event ,"Update an event");
		
		eventMapper.updateEntityFromDto(request, event);
		Event updated = eventRepository.save(event);
		
		return eventMapper.toResponse(updated);
		
	}

	/**
	 * Handle get all events in the database
	 * 
	 * @return an array list of all events
	 * */
	@Override
	 @Cacheable(value = ALL_EVENTS_CACHE)
	public List<EventResponse> getAllEvents() {

		List<Event> allEvent = eventRepository.findAll();
		List<EventResponse> response = new ArrayList<>();
		
		for (Event event : allEvent) {
			response.add(eventMapper.toResponse(event));
		}
		return response;
	}
	
	
	/**
	 * Handle publish an event.
	 * Before published checks ownership of events. 
	 * If this event belongs to another user, thowrs AccessDeniedException
	 * 
	 * @param Id number of event
	 * @return EventResponse as a dto object.
	 * @throws AccessDeniedException and EventNotFoundException if event is not found
	 * */
	@Override
	 @CachePut(value = EVENTS_CACHE, key = "#id")
    @CacheEvict(value = ALL_EVENTS_CACHE, allEntries = true)
	public EventResponse publish(Long id) {
		
		Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		
		checkOwnership(event ,"Publish an event");
		
	    if (event.getStatus() == EventStatus.CANCELLED) {
	        throw new InvalidEventStateException("A cancelled event cannot be cancelled.");
	    }
	    if (event.getStatus() == EventStatus.PUBLISHED) {
	        throw new InvalidEventStateException("The event has already been published.");
	    }
		
		event.setStatus(EventStatus.PUBLISHED);
		Event updated = eventRepository.save(event);
		
		return eventMapper.toResponse(updated);
	}

	/**
	 * Handle cancel an event.
	 * Before canceled checks ownership of events. 
	 * If this event belongs to another user, thowrs AccessDeniedException
	 * 
	 * @param Id number of event
	 * @return EventResponse as a dto object.
	 * @throws AccessDeniedException and EventNotFoundException if event is not found
	 * */
	@Override
    @CachePut(value = EVENTS_CACHE, key = "#id")
    @CacheEvict(value = ALL_EVENTS_CACHE, allEntries = true)
	public EventResponse cancel(Long id) {
		
		Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
		
		checkOwnership(event ,"Cancel an event");
		
		 if (event.getStatus() == EventStatus.CANCELLED) {
		        throw new InvalidEventStateException("The event has already been cancelled");
		    }
		
		event.setStatus(EventStatus.CANCELLED);
		Event updated = eventRepository.save(event);
		
		return eventMapper.toResponse(updated);
	}
	
	
	/**
	 * written to check owenership of the event
	 * compare username which getting from context with event's createdBy user's username
	 *
	 * @param An event object and error message as a string
	 * @throws AccessDeniedException when the event belongs to another user
	 */
	private void checkOwnership(Event event ,  String message) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String username = auth.getName();

	    if (!event.getCreatedBy().getEmail().equals(username)) {
	        throw new AccessDeniedException(message);
	    }
	}

}
