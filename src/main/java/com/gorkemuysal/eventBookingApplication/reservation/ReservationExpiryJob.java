package com.gorkemuysal.eventBookingApplication.reservation;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Periodically scans for PENDING reservations whose hold has expired
 * 
 * This class only decides "when" and "which ones"
 * It does not perform DB updates
 * 
 * */
@Component
@RequiredArgsConstructor
public class ReservationExpiryJob {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpiryJob.class);

    private final ReservationRepository reservationRepository;
    private final ReservationExpiryService expiryService;

    /**
     * Runs every 60 seconds
     * */
    @Scheduled(fixedDelay = 60_000)
    public void expirePendingReservations() {
        List<Reservation> expired = reservationRepository
                .findByStatusAndExpiresAtBefore(ReservationStatus.PENDING, Instant.now());

        if (expired.isEmpty()) {
            return;
        }

        log.info("Expiring {} pending reservation(s)", expired.size());

        for (Reservation r : expired) {
            try {
                expiryService.expireOne(r.getId());
            } catch (Exception e) {
                log.error("Failed to expire reservation {}", r.getId(), e);
            }
        }
    }
}
