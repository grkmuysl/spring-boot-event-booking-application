package com.gorkemuysal.eventBookingApplication.reservation.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gorkemuysal.eventBookingApplication.common.exception.EventNotAvailableException;
import com.gorkemuysal.eventBookingApplication.common.exception.EventNotFoundException;
import com.gorkemuysal.eventBookingApplication.common.exception.InsufficientCapacityException;
import com.gorkemuysal.eventBookingApplication.common.exception.InvalidReservationStateException;
import com.gorkemuysal.eventBookingApplication.common.exception.ReservationAccessDeniedException;
import com.gorkemuysal.eventBookingApplication.common.exception.ReservationNotFoundException;
import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.event.EventRepository;
import com.gorkemuysal.eventBookingApplication.event.EventStatus;
import com.gorkemuysal.eventBookingApplication.identity.User;
import com.gorkemuysal.eventBookingApplication.reservation.Reservation;
import com.gorkemuysal.eventBookingApplication.reservation.ReservationRepository;
import com.gorkemuysal.eventBookingApplication.reservation.ReservationStatus;
import com.gorkemuysal.eventBookingApplication.reservation.service.ReservationService;

import jakarta.transaction.Transactional;

// Main Service class of Reservation
@Service
public class ReservationServiceImpl implements ReservationService {

	private static final int EXPIRY_MINUTES = 15;

	private final EventRepository eventRepository;

	private final ReservationRepository reservationRepository;

	public ReservationServiceImpl(EventRepository eventRepository, ReservationRepository reservationRepository) {
		this.eventRepository = eventRepository;
		this.reservationRepository = reservationRepository;
	}

	/**
	 * Handle making reservation. Checks information of event
	 * 
	 * @param Id number of event, User object who makes reservation and requested seat count
	 * @return a Reservation object
	 * @throws EventNotFoundException when event not found with entered event ID number
	 * @throws EventNotAvailableException when event is already published
	 * @throws InsufficientCapacityException when avaliable seats of event is less than requested
	 * */
	@Override
	@Transactional
	public Reservation reserve(Long eventId, User user, int seatCount) {

		Event event = eventRepository.findByIdForUpdate(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

		if (event.getStatus() == EventStatus.PUBLISHED) {
			throw new EventNotAvailableException(eventId);
		}

		if (event.getAvailableSeats() < seatCount) {
			throw new InsufficientCapacityException(eventId, seatCount, event.getAvailableSeats());
		}

		event.setAvailableSeats(event.getAvailableSeats() - seatCount);

		Reservation reservation = buildPendingReservation(event, user, seatCount);

		return reservationRepository.save(reservation);
	}

	/**
	 * a helper method to pending reservation. Creates new event with PENDING status and date informatins
	 * 
	 * @param Event object, User object and number of seat reqeusted
	 * @return a Reservation object
	 * */
	private Reservation buildPendingReservation(Event event, User user, int seatCount) {

		Reservation reservation = new Reservation();
		reservation.setEvent(event);
		reservation.setCreatedBy(user);
		reservation.setSeatCount(seatCount);
		reservation.setStatus(ReservationStatus.PENDING);

		Instant now = Instant.now();
		reservation.setReservedAt(now);
		reservation.setExpiresAt(now.plus(EXPIRY_MINUTES, ChronoUnit.MINUTES));

		return reservation;
	}

	/**
	 * finds all reservations created by specific user
	 *
	 * @param User object and Pageable object
	 * @return page of Reservations
	 * 
	 * */
	@Override
	@Transactional
	public Page<Reservation> getReservations(User user, Pageable pageable) {
		return reservationRepository.findByCreatedById(user.getId(), pageable);
	}

	/**
	 * Handle confirm a reservation. Checks status, createdby and expires date informations
	 * 
	 * @param ID number of reservation which will confirm and User object who wants confirm the reservation
	 * @throws ReservationNotFoundException wheen reservation not found with entered reservationId
	 * @throws ReservationAccessDeniedException when reservation belongs to another user
	 * @throws InvalidReservationStateException when reservation status is not PENDING or reservation is already expired
	 * 
	 * */
	@Override
	@Transactional
	public Reservation confirm(Long reservationId, User user) {

		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new ReservationNotFoundException(reservationId));

		if (!reservation.getCreatedBy().getId().equals(user.getId())) {
			throw new ReservationAccessDeniedException(reservationId);
		}

		if (reservation.getStatus() != ReservationStatus.PENDING) {
			throw new InvalidReservationStateException(reservationId, reservation.getStatus());
		}

		if (reservation.getExpiresAt().isBefore(Instant.now())) {
			throw new InvalidReservationStateException(reservationId, reservation.getStatus());
		}

		reservation.setStatus(ReservationStatus.CONFIRMED);
		return reservation;
	}

}
