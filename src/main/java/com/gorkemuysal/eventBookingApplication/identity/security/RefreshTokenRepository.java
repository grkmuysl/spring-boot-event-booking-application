package com.gorkemuysal.eventBookingApplication.identity.security;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{
	Optional<RefreshToken> findByToken(String token);
	
	@Modifying
	@Transactional
	@Query("update RefreshToken r set r.revoked = true where r.user.id = :userId")
	void revokeAllByUserId(Long userId);
}
