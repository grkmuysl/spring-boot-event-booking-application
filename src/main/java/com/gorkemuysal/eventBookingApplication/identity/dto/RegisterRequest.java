package com.gorkemuysal.eventBookingApplication.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank @Email String email,
		@NotBlank @Size(max = 150) String fullname,
		@NotBlank  @Size(min = 3, max = 100) String password) {

}
