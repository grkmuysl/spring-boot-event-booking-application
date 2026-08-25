package com.gorkemuysal.eventBookingApplication.identity.dto;

import com.gorkemuysal.eventBookingApplication.identity.Roles;


public record UserResponse(String email, String fullName, Roles role) {

}
