package com.gorkemuysal.eventBookingApplication.reservation;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gorkemuysal.eventBookingApplication.reservation.dto.ReservationResponse;

//An interface to convert entities to dto objects or convert dto objects to entity objects
@Mapper(componentModel = "spring")
public interface ReservationMapper {

	@Mapping(target = "eventName", source = "event.title")
	@Mapping(target = "createdBy", source = "createdBy.fullName")
	ReservationResponse toDto(Reservation reservation);
	
	
	List<ReservationResponse> toDtoList(List<Reservation> reservations);
}
