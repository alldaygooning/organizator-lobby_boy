package pencil_utensil.lobby_boy.network.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pencil_utensil.lobby_boy.credentials.JwtService;
import pencil_utensil.lobby_boy.exception.user.InvalidCredentialsException;
import pencil_utensil.lobby_boy.exception.user.UnavailableNameException;
import pencil_utensil.lobby_boy.user.User;
import pencil_utensil.lobby_boy.user.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;
	private final JwtService jwtService;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	UserController(UserService userService, JwtService jwtService) {
		this.userService = userService;
		this.jwtService = jwtService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody Credentials credentials) {
		User user;
		try {
			user = userService.register(credentials.name, credentials.password);
		} catch (UnavailableNameException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("field.name", "credentials.name.unique"));
		}
		LOGGER.info("{} registered.", user.getName());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody Credentials credentials) {
		User user;
		try {
			user = userService.authenticate(credentials.name, credentials.password);
		} catch (InvalidCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("field.credentials", "credentials.invalid"));
		}
		LOGGER.info("{} logged in.", user.getName());
		return ResponseEntity.ok(Map.of("token", jwtService.issue(user.getId(), user.getName())));
	}

	public static class Credentials {
		@NotBlank(message = "credentials.name.notblank")
		@Size(min = 3, max = 50, message = "credentials.name.size")
		public String name;
		@NotBlank(message = "credentials.password.notblank")
		public String password;
	}
}
//	@PostMapping("/{id}") //fetch user by id
//	@PostMapping("/{name}") //fetch user by name
