package pencil_utensil.lobby_boy.credentials;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final int TTL = 86400000;
	private static final String USER_NAME_CLAIM = "username";
	private final SecretKey signingKey;

	private static final String JWT_COOKIE_NAME = "auth_token";
	private static final Duration JWT_COOKIE_MAX_AGE = Duration.ofHours(24);
	private static final String COOKIE_PATH = "/";
	private static final boolean HTTP_ONLY = true;
	private static final boolean SECURE = false; // HTTP only
	private static final String SAME_SITE = "Lax";

	public JwtService(@Value("${jwt.secret}") String jwtSecret) {
		this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public String issue(Integer userId, String username) {
		Long time = System.currentTimeMillis();
		String token = Jwts.builder()
				.subject(userId.toString())
				.claim(USER_NAME_CLAIM, username)
				.signWith(signingKey)
				.issuedAt(new Date(time))
				.expiration(new Date(time + TTL))
				.compact();
		return token;
	}

	public ResponseCookie createJwtCookie(String token) {
		return ResponseCookie.from(JWT_COOKIE_NAME, token)
				.maxAge(JWT_COOKIE_MAX_AGE)
				.path(COOKIE_PATH)
				.httpOnly(HTTP_ONLY)
				.secure(SECURE)
				.sameSite(SAME_SITE)
				.build();
	}

	public ResponseCookie issueJwtCookie(Integer userId, String username) {
		String token = issue(userId, username);
		return createJwtCookie(token);
	}

	public ResponseCookie issueEmptyJwtCookie() {
		return ResponseCookie.from(JWT_COOKIE_NAME, "")
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(JWT_COOKIE_MAX_AGE)
				.sameSite("Strict")
				.build();
	}

	public boolean validate(String token) {
		return getUserId(token).isPresent();
	}

	private Claims getPayload(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public Optional<Integer> getUserId(String token) {
		try {
			Claims payload = getPayload(token);
			return Optional.of(Integer.valueOf(payload.getSubject()));
		} catch (NumberFormatException | JwtException e) {
			return Optional.empty();
		}
	}

	public Optional<String> getUserName(String token) {
		try {
			Claims payload = getPayload(token);
			return Optional.ofNullable(payload.get(USER_NAME_CLAIM, String.class));
		} catch (JwtException e) {
			return Optional.empty();
		}
	}
}
