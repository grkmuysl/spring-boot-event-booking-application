package com.gorkemuysal.eventBookingApplication.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.gorkemuysal.eventBookingApplication.event.Event;
import com.gorkemuysal.eventBookingApplication.event.dto.EventRequest;
import com.gorkemuysal.eventBookingApplication.event.dto.EventResponse;

// An interface to convert entities to dto objects or convert dto objects to entity objects
@Mapper(componentModel = "spring")
public interface EventMapper {

	/**
	 * Convert EventRequest objects to Event entity.
	 * 
	 * @param A dto object which contains required fields
	 * @return An Event entity object
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "availableSeats", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	public Event toEntity(EventRequest request);

	/**
	 * Convert Event entity objects to EventDto object
	 * 
	 * @param An Event entity object which contains required fields
	 * @return A EventResponse object
	 */
	@Mapping(target = "createdByEmail", source = "createdBy.email")
	public EventResponse toDto(Event event);

	/**
	 * Thanks to this method an Entity object can be updated automatically with Dto
	 * object
	 * 
	 * @param An EventRequest object and An Event object
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "availableSeats", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	void updateEntityFromDto(EventRequest request, @MappingTarget Event event);
}
