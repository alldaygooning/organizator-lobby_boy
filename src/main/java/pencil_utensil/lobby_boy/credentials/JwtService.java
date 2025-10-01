package pencil_utensil.lobby_boy.credentials;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private static final int TTL = 86400000;
	private final SecretKey signingKey;

	public JwtService(@Value("${jwt.secret}") String jwtSecret) {
		this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public String issue(Integer userId, String username) {
		Long time = System.currentTimeMillis();
		String token = Jwts.builder()
				.subject(userId.toString())
				.claim("username", username)
				.signWith(signingKey)
				.issuedAt(new Date(time))
				.expiration(new Date(time + TTL))
				.compact();
		return token;
	}

	public boolean validate(String token) {
		try {
			Claims payload = getPayload(token);
			Integer userId = Integer.valueOf(payload.getSubject());
			return userId != null;
		} catch (NumberFormatException | JwtException e) {
			return false;
		}
	}

	private Claims getPayload(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
