package com.gorkemuysal.eventBookingApplication.event;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gorkemuysal.eventBookingApplication.event.dto.EventRequest;
import com.gorkemuysal.eventBookingApplication.event.dto.EventResponse;
import com.gorkemuysal.eventBookingApplication.event.service.EventService;

import jakarta.validation.Valid;

// A main controller class of Events API calls
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

	private final EventService eventService;

	public EventController(EventService eventService) {
		this.eventService = eventService;
	}

	/**
	 * Handle create new event
	 * 
	 * @param A request object contains the details of the Event to be created.
	 * @return 201 CREATED response body
	 */
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {

		EventResponse response = eventService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);

	}

	/**
	 * Handle get all events as a array list of EventResponse
	 * 
	 * @return 200 OK response body
	 */
	@GetMapping
	public ResponseEntity<List<EventResponse>> getAllEvents() {
		List<EventResponse> response = eventService.getAllEvents();

		return ResponseEntity.ok(response);
	}

	/**
	 * Handle get event with a specific ID
	 * 
	 * @param ID number of events as a Long
	 * @return 200 OK response body
	 */
	@GetMapping("/{id}")
	public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
		EventResponse response = eventService.getById(id);

		return ResponseEntity.ok(response);

	}

	/**
	 * Handle update event according to request body
	 * 
	 * @param ID number of events as a Long and request body as a Eventrequest
	 *           object
	 * @return 200 OK response body
	 */
	@PutMapping("/{id}")
	public ResponseEntity<EventResponse> update(@Valid @RequestBody EventRequest request, @PathVariable Long id) {

		EventResponse response = eventService.update(request, id);

		return ResponseEntity.ok(response);

	}

	/**
	 * Handle publish event with a specifc Id number Contains Authorize control.
	 * Only ADMIN role can change status of events.
	 * 
	 * @param ID number of events as a Long
	 * @return 200 OK response body
	 */
	@PatchMapping("/{id}/publish")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<EventResponse> publish(@PathVariable Long id) {

		EventResponse response = eventService.publish(id);

		return ResponseEntity.ok(response);
	}

	/**
	 * Handle cancel event with a specifc Id number Contains Authorize control. Only
	 * ADMIN role can change status of events.
	 * 
	 * @param ID number of events as a Long
	 * @return 200 OK response body
	 */
	@PatchMapping("/{id}/cancel")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<EventResponse> cancel(@PathVariable Long id) {

		EventResponse response = eventService.cancel(id);

		return ResponseEntity.ok(response);
	}

}
