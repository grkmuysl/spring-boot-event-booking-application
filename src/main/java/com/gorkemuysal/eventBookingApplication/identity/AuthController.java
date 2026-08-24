package com.gorkemuysal.eventBookingApplication.identity;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.gorkemuysal.eventBookingApplication.identity.dto.LoginRequest;
import com.gorkemuysal.eventBookingApplication.identity.dto.RegisterRequest;
import com.gorkemuysal.eventBookingApplication.identity.security.JwtService;
import com.gorkemuysal.eventBookingApplication.identity.security.UserPrincipal;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	
	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}
	
	@PostMapping("/register")
	ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request, UriComponentsBuilder uriBuilder) throws Exception{
		
		if(!userRepository.existsByEmail(request.email())) {
			throw new Exception("user not found.");
		}
		
		User user = new User();
		user.setEmail(request.email());
		user.setFullName(request.fullname());
		user.setPasswordHash(request.password());
		
		userRepository.save(user);
		
		URI location = uriBuilder.path("/api/v1/users/{id}").buildAndExpand(user.getId()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PostMapping("/login")
	String login(@Valid @RequestBody LoginRequest request) throws Exception {
		User user = userRepository.findByEmail(request.email())
						.orElseThrow();
		
		if(!passwordEncoder.matches(user.getPasswordHash() , request.password())) {
			throw new Exception("passwords not matchs");
		}
		
		
		
		String accessToken = jwtService.generateAccessToken(new UserPrincipal(user));
		
		return accessToken;
	}
}
