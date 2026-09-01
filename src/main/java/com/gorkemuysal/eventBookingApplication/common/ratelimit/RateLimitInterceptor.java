package com.gorkemuysal.eventBookingApplication.common.ratelimit;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

	private final RateLimitingService rateLimitingService;

	public RateLimitInterceptor(RateLimitingService rateLimitingService) {
		this.rateLimitingService = rateLimitingService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String uri = request.getRequestURI();
		String method = request.getMethod();

		String clientKey;
		PlanType planType;

		if (uri.startsWith("/api/v1/auth/login") || uri.startsWith("/api/v1/auth/register")) {
			// Auth operations are restricted based on API
			clientKey = getClientIP(request) + ":" + uri;
			planType = PlanType.AUTH_LOGIN;
		} else if (uri.startsWith("/api/v1/reservations") && "POST".equalsIgnoreCase(method)) {
			// Reservation api calls are restricted based on logged in users
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String userId = (auth != null && auth.isAuthenticated()) ? auth.getName() : getClientIP(request);
			clientKey = "USER:" + userId + ":" + uri;
			planType = PlanType.RESERVATION;
		} else {

			clientKey = getClientIP(request);
			planType = PlanType.GENERAL_PUBLIC;
		}

		Bucket bucket = rateLimitingService.resolveBucket(clientKey, planType);
		ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

		if (probe.isConsumed()) {
			response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
			return true;
		} else {
			long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
			response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
			response.addHeader("Retry-After", String.valueOf(waitForRefill));
			response.setContentType("application/json");
			response.getWriter().write("""
					    {"error": "Too many requests", "retryAfterSeconds": %d}
					""".formatted(waitForRefill));
			return false;
		}
	}

	private String getClientIP(HttpServletRequest request) {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null || xfHeader.isEmpty()) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0].trim();
	}
}