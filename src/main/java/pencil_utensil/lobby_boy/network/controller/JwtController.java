package pencil_utensil.lobby_boy.network.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import pencil_utensil.lobby_boy.credentials.JwtService;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {

	private final JwtService jwtService;

	JwtController(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@PostMapping("/validate")
	public ResponseEntity<?> validate(@Valid @RequestBody JWTRequest request) {
		String token = request.token;
		boolean valid = jwtService.validate(token);
		Map<String, Object> body = new HashMap<>();
		body.put("valid", valid);
		if (valid) {
			body.put("name", jwtService.getUserName(token));
			body.put("id", jwtService.getUserId(token));
		}
		return ResponseEntity.ok(body);
	}

	public static class JWTRequest {
		@NotBlank(message = "jwt.notblank")
		@Size(min = 5, max = 1000, message = "jwt.size")
		public String token;
	}
}
