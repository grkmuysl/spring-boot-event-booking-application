package com.gorkemuysal.eventBookingApplication.identity.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gorkemuysal.eventBookingApplication.identity.Roles;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service responsible for JWT operations
 * 
 */

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private Duration jwtExpiration;

	/**
	 * Decodes the Base64 encoded secret key and generates an HMAC signing key
	 * 
	 * @return The {@link SecretKey} used for signing and verifying JWTs
	 */
	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * Generates a signed JWT access token containing the user's ID a subject, email
	 * and roles as a custom claims
	 *
	 * @param principal the authenticated user's principal details
	 * @return A signed JWT string
	 */

	public String generateAccessToken(UserPrincipal principal) {

		Instant now = Instant.now();
		Instant expiry = now.plus(jwtExpiration);

		return Jwts.builder().subject(principal.getUserId()).claim("email", principal.getUsername())
				.claim("role", principal.getUserRole().name()).issuedAt(Date.from(now)).expiration(Date.from(expiry))
				.signWith(getSignInKey()).compact();
	}

	/**
	 * Extract userId from the token
	 */

	public Long extractUserId(String token) {
		String userId = parseClaims(token).getSubject();

		return Long.valueOf(userId);
	}

	/**
	 * Extract email from the token
	 */

	public String extractEmail(String token) {

		return parseClaims(token).get("email", String.class);
	}

	/**
	 * Extract role from the token
	 */

	public Roles extractRoles(String token) {
		String role = parseClaims(token).get("role", String.class);
		return Roles.valueOf(role);
	}

	/**
	 * Parses and validates the JWT payload using the signing key.
	 * */
	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
	}

	/**
	 * Extrat expiration timestamp from the token
	 * */
	private Instant extractExpiration(String token) {
		return parseClaims(token).getExpiration().toInstant();
	}

	
	/**
	 * checks if the jwt token is expired
	 * */
	
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).isBefore(Instant.now());
	}

	/**
	 * checks the token belongs to the given user principal and the token is not expired
	 * 
	 * */
	public boolean isTokenValid(String token) {
	    try {
	        return !isTokenExpired(token);
	    } catch (JwtException e) {
	        return false;
	    }
	}
	
}
