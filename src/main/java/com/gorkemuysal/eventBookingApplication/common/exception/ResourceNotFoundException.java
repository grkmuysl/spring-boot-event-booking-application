package com.gorkemuysal.eventBookingApplication.common.exception;

public class ResourceNotFoundException extends RuntimeException{

	public ResourceNotFoundException(String message) {
		super(message);
	}
	
	public ResourceNotFoundException(String entityName, Object id) {
		super(entityName + " not found with id: " + id);
	}
}
