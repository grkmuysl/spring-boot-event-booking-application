package com.gorkemuysal.eventBookingApplication.identity.security;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gorkemuysal.eventBookingApplication.identity.Roles;
import com.gorkemuysal.eventBookingApplication.identity.User;

/**
 * Adapter class that bridges the domain {@link User} entity with Spring Security's {@link UserDetails}.
 * Provied user identity and role-based authority details for authentication
 * */
public class UserPrincipal implements UserDetails {

	private final User user;

	
	/**
	 * Constructs a new UserPrincipal wrapping the given domain user
	 * 
	 * @param user The domain user entity
	 * */
	public UserPrincipal(User user) {
		this.user = user;
	}

	/**
	 * returns the authorities granted to the user
	 * 
	 * @return A collection containing the granted authorities
	 * */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
	}

	
	/**
	 * Returns the hashed password of user
	 * 
	 * @return The password hash
	 * */
	
	@Override
	public @Nullable String getPassword() {

		return user.getPasswordHash();
	}

	/**
	 * Returns the username used to authenticate the user.
	 * In this project the email address serves as the primary username
	 * 
	 * @return The user's email address
	 * */
	@Override
	public String getUsername() {
		return user.getEmail();
	}

	
	/**
	 * Returns the unique user ID as a String
	 * 
	 * @return The user ID.
	 * */
	public String getUserId() {
		return user.getId().toString();
	}

	
	/**
	 * Retrieves the underlying domain {@link User} entity.
	 * 
	 * @return The wrapped user entity
	 * */
	public User getUser() {
		return user;
	}

	/**
	 * retrieves the enum of the user
	 * 
	 * @return The user's {@link Roles}
	 * */
	public Roles getUserRole() {
		return user.getRole();
	}

}
