package com.gorkemuysal.eventBookingApplication.identity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.gorkemuysal.eventBookingApplication.common.exception.EmailAlreadyExistsException;
import com.gorkemuysal.eventBookingApplication.common.exception.InvalidCredentialsException;
import com.gorkemuysal.eventBookingApplication.identity.dto.LoginRequest;
import com.gorkemuysal.eventBookingApplication.identity.dto.RefreshTokenRequest;
import com.gorkemuysal.eventBookingApplication.identity.dto.RegisterRequest;
import com.gorkemuysal.eventBookingApplication.identity.dto.TokenResponse;
import com.gorkemuysal.eventBookingApplication.identity.dto.UserResponse;
import com.gorkemuysal.eventBookingApplication.identity.security.JwtService;
import com.gorkemuysal.eventBookingApplication.identity.security.RefreshToken;
import com.gorkemuysal.eventBookingApplication.identity.security.RefreshTokenService;
import com.gorkemuysal.eventBookingApplication.identity.security.UserPrincipal;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;

	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
			RefreshTokenService refreshTokenService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.refreshTokenService = refreshTokenService;
	}

	@PostMapping("/register")
	ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request, UriComponentsBuilder uriBuilder)
			throws Exception {

		if (userRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException(request.email());
		}

		User user = new User();
		user.setEmail(request.email());
		user.setFullName(request.fullname());
		user.setPasswordHash(passwordEncoder.encode(request.password()));

		userRepository.save(user);

		URI location = uriBuilder.path("/api/v1/users/{id}").buildAndExpand(user.getId()).toUri();
		return ResponseEntity.created(location).build();
	}

	@PostMapping("/login")
	TokenResponse login(@Valid @RequestBody LoginRequest request) throws Exception {

		// Same exception for "email not found" and "wrong password"
		// to avoid leaking which emails are registered.
		User user = userRepository.findByEmail(request.email()).orElseThrow(InvalidCredentialsException::new);

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new InvalidCredentialsException();
		}

		String accessToken = jwtService.generateAccessToken(new UserPrincipal(user));
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

		return new TokenResponse(accessToken, refreshToken.getToken());
	}

	/**
	 * Issues a new access token and rotates the refresh token Given a valid,
	 * non-expired, non-revoked refresh token
	 */
	@PostMapping("/refresh-token")
	TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
		RefreshToken validated = refreshTokenService.verify(request.refreshToken());
		RefreshToken rotated = refreshTokenService.rotate(validated);

		User user = validated.getUser();
		String newAccessToken = jwtService.generateAccessToken(new UserPrincipal(user));

		return new TokenResponse(newAccessToken, rotated.getToken());
	}

	// Revokes all refresh tokens belonging to a authenticated user
	@PostMapping("/logout")
	ResponseEntity<Void> logout(@AuthenticationPrincipal UserPrincipal principal) {
		refreshTokenService.revokeAllForUser(Long.valueOf(principal.getUserId()));
		return ResponseEntity.noContent().build();
	}

	/**
	 * a get api call to show all users as a DTO object list.
	 * 
	 * @return returns List of User Dto objects
	 */
	@GetMapping("/users")
	List<UserResponse> getAllUsers() {

		return userRepository.findAll().stream()
				.map(user -> new UserResponse(user.getEmail(), user.getFullName(), user.getRole())).toList();

	}

}
