package com.gorkemuysal.eventBookingApplication.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Email
	@Column(name = "email", nullable = false, unique = true)
	private String email;

	private String passwordHash;

	@Column(name = "full_name", nullable = false)
	private String fullName;	

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Roles role = Roles.USER;

}
