package com.gorkemuysal.eventBookingApplication.event;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

public interface EventRepository extends JpaRepository<Event, Long> {

	Optional<Event> findById(Long id);
	
	List<Event> findAll();
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
	@Query("select e from Event e where e.id = :id")
	Optional<Event> findByIdForUpdate(@Param("id") Long id);
}
