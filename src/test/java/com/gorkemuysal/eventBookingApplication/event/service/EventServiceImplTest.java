package com.gorkemuysal.eventBookingApplication.event.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.gorkemuysal.eventBookingApplication.common.exception.EventNotFoundException;
import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.event.EventRepository;
import com.gorkemuysal.eventBookingApplication.event.EventStatus;
import com.gorkemuysal.eventBookingApplication.event.dto.EventRequest;
import com.gorkemuysal.eventBookingApplication.event.dto.EventResponse;
import com.gorkemuysal.eventBookingApplication.event.mapper.EventMapper;
import com.gorkemuysal.eventBookingApplication.event.service.impl.EventServiceImpl;
import com.gorkemuysal.eventBookingApplication.identity.User;
import com.gorkemuysal.eventBookingApplication.identity.UserRepository;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private EventMapper eventMapper;
	@Mock
	private EventRepository eventRepository;
	@Mock
	private Authentication authentication;
	@Mock
	private SecurityContext securityContext;

	@InjectMocks
	private EventServiceImpl eventService;

	private User currentUser;
	private EventRequest request;

	@BeforeEach
	void setUp() {

		currentUser = new User();
		currentUser.setEmail("gorkem@test.com");

		request = new EventRequest("test title", "test descripton", "test vanue", LocalDateTime.of(2026, 6, 15, 10, 0),
				100, BigDecimal.valueOf(250), EventStatus.DRAFT);

		SecurityContextHolder.setContext(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void create_shouldReturnEventResponse_whenUserExists() {

		when(authentication.getName()).thenReturn("gorkem@test.com");
		when(userRepository.findByEmail("gorkem@test.com")).thenReturn(Optional.of(currentUser));

		Event mappedEvent = new Event();
		when(eventMapper.toEntity(request)).thenReturn(mappedEvent);

		Event savedEvent = new Event();
		savedEvent.setId(1L);
		when(eventRepository.save(mappedEvent)).thenReturn(savedEvent);

		EventResponse expectedResponse = new EventResponse(1L, "test title", "test descripton", "test vanue",
				request.startTime(), 100, BigDecimal.valueOf(250), EventStatus.DRAFT, "gorkem@test.com");

		when(eventMapper.toResponse(savedEvent)).thenReturn(expectedResponse);

		// when
		EventResponse result = eventService.create(request);

		// then
		assertEquals(expectedResponse, result);
		assertEquals(currentUser, mappedEvent.getCreatedBy());
		assertEquals(EventStatus.DRAFT, mappedEvent.getStatus());
		assertEquals(100, mappedEvent.getAvailableSeats());
		verify(eventRepository).save(mappedEvent);
	}

	@Test
	void create_shouldThrowException_whenUserNotFound() {
		when(authentication.getName()).thenReturn("unknown@test.com");
		when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> eventService.create(request));

		verify(eventRepository, never()).save(any());
		verify(eventMapper, never()).toEntity(any());
	}
	
	@Test
	void getById_shouldReturnEventResponse_whenEventExists() {
	    // given
	    Long eventId = 1L;
	    Event event = new Event();
	    event.setId(eventId);

	    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

	    EventResponse expectedResponse = new EventResponse(
	            eventId, "test title", "test descripton",
	            "test vanue", LocalDateTime.now(),
	            100, BigDecimal.valueOf(250), EventStatus.DRAFT,
	            "gorkem@test.com"
	    );
	    when(eventMapper.toResponse(event)).thenReturn(expectedResponse);

	    // when
	    EventResponse result = eventService.getById(eventId);

	    // then
	    assertEquals(expectedResponse, result);
	    verify(eventRepository).findById(eventId);
	}
	
	@Test
	void getById_shouldThrowException_whenEventNotFound() {
	    // given
	    Long eventId = 99L;
	    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

	    // when + then
	    assertThrows(EventNotFoundException.class,
	            () -> eventService.getById(eventId));

	    verify(eventMapper, never()).toResponse(any(Event.class));
	}

}
