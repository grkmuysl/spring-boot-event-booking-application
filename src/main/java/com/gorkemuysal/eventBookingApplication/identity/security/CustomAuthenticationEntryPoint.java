package com.gorkemuysal.eventBookingApplication.identity.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

/**
 * This class is written to handle unauthenticated access or access with expired (or invalid) token.
 * Thanks to {@link ProblemDetail} class we can use customized JSON error responses
 * */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

	private final ObjectMapper objectMapper;

	CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.UNAUTHORIZED, "Authentication required or token invalid.");
		problem.setTitle("Unauthorized access");
		problem.setProperty("timestamp", Instant.now());

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), problem);
		
	}

}
