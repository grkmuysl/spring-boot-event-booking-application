package com.gorkemuysal.eventBookingApplication.reservation;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// Main Repository interface for the Reservation class
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	/**
	 * Get Reservations according to their status and check them 
	 * If they didn't expires yet. Return them
	 * 
	 * @param status as a ReservationStatus enum and date information as a Instant object
	 * @return List of Reservations
	 * */
	List<Reservation> findByStatusAndExpiresAtBefore(ReservationStatus status, Instant now);

	/**
	 * Finds and filter Reservations according to Users
	 * Return them pageable 
	 * 
	 * @param userId as a Long and pageable object
	 * @return Page of Reservations
	 * */
	Page<Reservation> findByCreatedById(Long userId, Pageable pageable);
}
