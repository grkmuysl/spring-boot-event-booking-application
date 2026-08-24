package com.gorkemuysal.eventBookingApplication.identity.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;


/**
 * This class is written to handle HTTP 403 Forbidden errors
 * Thanks to {@link ProblemDetail} class we can use customized JSON error responses
 * */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	
	private final ObjectMapper objectMapper;

	CustomAccessDeniedHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		ProblemDetail problem = ProblemDetail.forStatusAndDetail(
				HttpStatus.FORBIDDEN, "You do not have permission for this operation.");
		problem.setTitle("Access denied");
		problem.setProperty("timestamp", Instant.now());

		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(response.getWriter(), problem);
		
		
	}

}
