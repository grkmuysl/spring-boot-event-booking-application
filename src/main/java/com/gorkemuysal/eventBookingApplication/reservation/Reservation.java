package com.gorkemuysal.eventBookingApplication.reservation;

import java.time.Instant;

import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.identity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservation")
@NoArgsConstructor
@Getter
@Setter
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "seat_count" , nullable = false)
	@Min(1)
	@NotNull
	private Integer seatCount;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReservationStatus status;
	
	@NotNull
	@Column(name = "reserved_at" , nullable = false)
	private Instant reservedAt;
	
	@NotNull
	@Column(name = "expires_at" , nullable = false)
	private Instant expiresAt;
}
