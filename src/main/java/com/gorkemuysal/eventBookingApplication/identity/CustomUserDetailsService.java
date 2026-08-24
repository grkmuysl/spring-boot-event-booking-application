package com.gorkemuysal.eventBookingApplication.identity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gorkemuysal.eventBookingApplication.identity.security.UserPrincipal;

/**
 * Custom implementation of {@link UserDetailsService} that retrieves user
 * authentication details from the database via {@link UserRepository}.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	/**
	 * Constructs a new {@code CustomUserDetailsService} with the user data
	 * repository
	 */
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Locates the user on the email (used as username)
	 * 
	 * @param email The email identifying the user whose data is required
	 * @return A fully populated {@link UserDetails} instance wrapped in a
	 *         {@link UserPrincipal}
	 * @throws UsernameNotFoundException If the user could not be found with the
	 *                                   given email
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("user not found: " + email));

		return new UserPrincipal(user);
	}

}
