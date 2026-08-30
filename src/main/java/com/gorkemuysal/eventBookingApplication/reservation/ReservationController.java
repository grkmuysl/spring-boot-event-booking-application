package com.gorkemuysal.eventBookingApplication.reservation;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gorkemuysal.eventBookingApplication.identity.User;
import com.gorkemuysal.eventBookingApplication.reservation.dto.ReservationRequest;
import com.gorkemuysal.eventBookingApplication.reservation.dto.ReservationResponse;
import com.gorkemuysal.eventBookingApplication.reservation.service.ReservationService;

import jakarta.validation.Valid;

// Main controller class to reservations
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

	private final ReservationService reservationService;
	private final ReservationMapper mapper;

	public ReservationController(ReservationService reservationService, ReservationMapper mapper) {
		this.reservationService = reservationService;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<ReservationResponse> reserve(@Valid @RequestBody ReservationRequest request,
			@AuthenticationPrincipal User user) {

		Reservation reservation = reservationService.reserve(request.eventId(), user, request.seatCount());
		ReservationResponse response = mapper.toDto(reservation);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(request.eventId())
				.toUri();

		return ResponseEntity.created(location).body(response);
	}

	@GetMapping
	public Page<ReservationResponse> getReservations(@AuthenticationPrincipal User user,
			@PageableDefault(size = 20, sort = "reservedAt", direction = Sort.Direction.DESC) Pageable pageable) {

		return reservationService.getReservations(user, pageable).map(mapper::toDto);

	}

	@PostMapping("/{id}/confirm")
	public ResponseEntity<ReservationResponse> confirm(@AuthenticationPrincipal User user,
			@PathVariable("id") Long id) {

		Reservation reservation = reservationService.confirm(id, user);
		return ResponseEntity.ok(mapper.toDto(reservation));
	}

}
