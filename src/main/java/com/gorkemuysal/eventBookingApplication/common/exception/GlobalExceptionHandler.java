package com.gorkemuysal.eventBookingApplication.common.exception;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Main exception handler for all REST controllers Converts exception thrown
 * anywhere in the controller/service layer into a consistent
 * {@link ProblemDetail} response body.
 * 
 * Note: this class NOT handle exceptions thrown from the Spring Security filter
 * chain. Those are handled by {@code CustomAuthenticationEntryPoint} and
 * {@code CustomAccessDeniedHandler}
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/*
	 * Handles cases where a request resource does not exist. Logged at WARN level
	 * because this is an excepted client error, not a server bug.
	 * 
	 * @param ex the thrown exception containing the error message
	 * 
	 * @return 404 NOT_FOUND problem detail response
	 */
	@ExceptionHandler(ResourceNotFoundException.class)
	public ProblemDetail handleNotFound(ResourceNotFoundException ex) {

		log.warn("Resource not found: {}", ex.getMessage());

		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problem.setTitle("Resource not found");
		problem.setProperty("timestamp", Instant.now());

		return problem;
	}

	/**
	 * Handles validation failures triggered by {@code Valid} on request body.
	 * 
	 * @param ex the thrown exception containing the error message
	 * @return 400 BAD_REQUEST problem detail response
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleNotValidArgument(MethodArgumentNotValidException ex) {

		log.warn("Validation failed: {}", ex.getMessage());

		Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(
				FieldError::getField,
				fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
				(existing, replacement) -> existing));

		ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problem.setTitle("Validation failed");
		problem.setProperty("errors", errors);
		problem.setProperty("timestamp", Instant.now());
		return problem;
	}
	
	/**
	 * Handles register failures when an email already exists 
	 * 
	 * @param ex the email already exists exception contains error message
	 * @return 409 CONFLICT problem detail response
	 * */

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ProblemDetail handleEmailExists(EmailAlreadyExistsException ex) {

		log.warn("Registration conflict: {}", ex.getMessage());

		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());

		problem.setTitle("Email already registered");
		problem.setProperty("timestamp", Instant.now());
		return problem;

	}
	
	/**
	 * Handles authentication failures. When an email and password does not matches with database
	 * 
	 * @param ex the invalid creaditials exception contains error message
	 * @return 401 UNAUTHORIZED problem detail response
	 * 
	 * */
	@ExceptionHandler(InvalidCredentialsException.class)
	public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex) {

		  log.warn("Authentication failed: {}", ex.getMessage());
		  
		  ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
		  problem.setTitle("Invalid credentials");
		    problem.setProperty("timestamp", Instant.now());
		    return problem;

	}
	
	/**
	 * handles any exception not caught by a more specific handler above Logged at
	 * ERROR level with the full stack trace since this is a unexcepted server-side
	 * failure
	 * 
	 * @param ex the unhandled exception
	 * @return a generic 500 INTERNAL_SERVER_ERROR
	 */
	@ExceptionHandler(Exception.class)
	public ProblemDetail generalException(Exception ex) {

		log.error("Unhandled exception occurred", ex);

		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"An unexpected error occurred.");
		problem.setTitle("Internal server error");
		problem.setProperty("timestamp", Instant.now());

		return problem;
	}

}
