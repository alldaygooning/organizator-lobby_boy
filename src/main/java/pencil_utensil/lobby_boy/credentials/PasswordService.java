package pencil_utensil.lobby_boy.credentials;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class PasswordService {

	private static final int SALT_LENGTH = 10;
	private static final int PEPPER_LENGTH = 1;
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	public PasswordCredentials hash(String password) {
		String salt = getSalt();
		String pepper = getPepper();
		return new PasswordCredentials(hash(password, salt, pepper), salt);
	}

	public boolean match(String hash, String password, String salt) {
		for (int i = 0; i < 256; i++) {
			String pepper = String.valueOf((char) i);
			String hashedAttempt = hash(password, salt, pepper);
			if (hashedAttempt.equals(hash)) {
				return true;
			}
		}
		return false;
	}

	private String hash(String password, String salt, String pepper) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String combinedPassword = password + salt + pepper;
			byte[] hash = digest.digest(combinedPassword.getBytes());
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Hashing algorithm not found", e);
		}
	}

	private String getSalt() { return getRandomString(SALT_LENGTH); }

	private String getPepper() { return getRandomString(PEPPER_LENGTH); }

	private String getRandomString(int length) {
		byte[] randomBytes = new byte[length];
		SECURE_RANDOM.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, length);
	}
}
