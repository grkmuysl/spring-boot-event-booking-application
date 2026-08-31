package com.gorkemuysal.eventBookingApplication.identity.security;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gorkemuysal.eventBookingApplication.common.exception.RefreshTokenException;
import com.gorkemuysal.eventBookingApplication.identity.User;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${jwt.refresh-expiration}")
	private long refreshTokenMs;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
	}

	// Creates and persists a new refresh token for the given user
	public RefreshToken createRefreshToken(User user) {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setUser(user);
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenMs));
		return refreshTokenRepository.save(refreshToken);
	}
	
	/**
	 * Validates the token: must exist, not be revoked, not be expired
	 * 
	 * throws an error according to messages
	 * */
	public RefreshToken verify(String tokenValue) {
		RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
				.orElseThrow(() -> new RefreshTokenException("Refresh token not found"));
		
		if(token.isRevoked()) {
			throw new RefreshTokenException("Refresh token is revoked");
		}
		
		if(token.getExpiryDate().isBefore(Instant.now())) {
			throw new RefreshTokenException("The refresh token has expired. Please log in again");
		}
		
		return token;
	}
	
	// Rotates token. Old token is revoked, generates brand-new one
	@Transactional
	public RefreshToken rotate(RefreshToken oldToken) {
		oldToken.setRevoked(true);
		
		refreshTokenRepository.save(oldToken);
		return createRefreshToken(oldToken.getUser());
	}
	
	@Transactional
	public void revokeAllForUser(Long userId) {
		refreshTokenRepository.revokeAllByUserId(userId);
	}
}
