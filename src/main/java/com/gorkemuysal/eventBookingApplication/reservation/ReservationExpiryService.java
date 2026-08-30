package com.gorkemuysal.eventBookingApplication.reservation;

import org.springframework.stereotype.Service;

import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.event.EventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Perform the actual expiry of a single reservationç
 * 
 * Called only from ReservationExpiryJob
 * */
@Service
@RequiredArgsConstructor
public class ReservationExpiryService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;

    /**	
     * Everything here happens in a single transaction.
     * */
    @Transactional
    public void expireOne(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            return; 
        }

        Event event = eventRepository.findByIdForUpdate(reservation.getEvent().getId()).orElseThrow();
        event.setAvailableSeats(event.getAvailableSeats() + reservation.getSeatCount());

        reservation.setStatus(ReservationStatus.EXPIRED);
    }
}
