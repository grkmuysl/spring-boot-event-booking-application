package com.gorkemuysal.eventBookingApplication.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(name = "title")
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@NotBlank
	@Column(name = "vanue")
	private String vanue;

	@NotNull
	@Column(name = "start_time", nullable = false)
	private LocalDateTime startTime;

	@NotNull
	@Min(1)
	@Column(name = "capacity", nullable = false)
	private Integer capacity;

	@NotNull
	@Min(0)
	@Column(nullable = false)
	private Integer availableSeats = capacity;

	@NotNull
	@DecimalMin(value = "0.0")
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private EventStatus status = EventStatus.DRAFT;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

}
