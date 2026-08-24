package com.gorkemuysal.eventBookingApplication.identity.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gorkemuysal.eventBookingApplication.identity.Roles;
import com.gorkemuysal.eventBookingApplication.identity.User;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private static final String HEADER_NAME = "Authorization";
	private static final String PREFIX = "Bearer ";
	
	private final JwtService jwtService;
	
	/**
	 * Constructs a new {@code JwtAuthenticationFilter} with required fields
	 * */
	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}
	
	
	/**
	 * Perform a actual filtering logic for authenticating requests via JWT.
	 * 
	 * @param request     The incoming HTTP servlet request
	 * @param response    The outgoing HTTP servlet response
	 * @param filterChain The filter chain to execute
	 * @throws ServletException If a servlet-specific error occurs
	 * @throws IOException      If an I/O error occurs during request processing
	 * */

	@Override
	protected void doFilterInternal(@Nonnull HttpServletRequest request, 
			@Nonnull HttpServletResponse response, 
			@Nonnull FilterChain filterChain)
			throws ServletException, IOException {
	
		String token = extractToken(request);
		
		
		/**
		 * Authenticate if token is not null and token is valid.
		 * */
		if(token != null && jwtService.isTokenValid(token)) {
			
			Long customerId = jwtService.extractUserId(token);
			String email = jwtService.extractEmail(token);
			Roles role = jwtService.extractRoles(token);
			
			User user = new User();
			user.setId(customerId);
			user.setEmail(email);
			user.setRole(role);
			
			UserPrincipal principal = new UserPrincipal(user);
			
			var authorities = List.of(new SimpleGrantedAuthority(role.name()));
			
			var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		}
		filterChain.doFilter(request, response);
		
	}
	
	/**
	 * If the Authorization header is missing or does not start with "Bearer ", skip authentication
	 * otherwise return token
	 * */
	private String extractToken(HttpServletRequest request) {
		String header = request.getHeader(HEADER_NAME);
		
		if(header != null && header.startsWith(PREFIX)) {
			return header.substring(PREFIX.length());
		}
		return null;
	}

}
