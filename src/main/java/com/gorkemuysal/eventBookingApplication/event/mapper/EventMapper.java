package com.gorkemuysal.eventBookingApplication.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.event.dto.EventDto;

// An interface to convert entities to dto objects or convert dto objects to entity objects
@Mapper(componentModel = "spring")
public interface EventMapper {

	
	/**
	 * Convert EventDto objects to Event entity.
	 * 
	 * @param A dto object which contains required fields
	 * @return An Event entity object
	 * */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "availableSeats", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	public Event toEntity(EventDto request);

	/**
	 * Convert Event entity objects to EventDto object
	 * 
	 * @param An Event entity object which contains required fields
	 * @return A EventDto object
	 * */
	@Mapping(target = "createdByEmail", source = "createdBy.email")
	public EventDto toDto(Event event);

	
	/**
	 * Thanks to this method an Entity object can be updated automatically with Dto object
	 * 
	 * @param An EventDto object and An Event object
	 * */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "availableSeats", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	void updateEntityFromDto(EventDto request, @MappingTarget Event event);
}
