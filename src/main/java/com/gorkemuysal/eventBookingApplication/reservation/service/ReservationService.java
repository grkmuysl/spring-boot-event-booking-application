package com.gorkemuysal.eventBookingApplication.reservation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gorkemuysal.eventBookingApplication.identity.User;
import com.gorkemuysal.eventBookingApplication.reservation.Reservation;

public interface ReservationService {

	public Reservation reserve(Long eventId, User user, int seatCount);
	
	public Page<Reservation> getReservations(User user, Pageable pageable);
	
	public Reservation confirm(Long reservationId, User user);
}
