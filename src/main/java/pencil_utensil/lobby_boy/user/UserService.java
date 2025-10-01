package pencil_utensil.lobby_boy.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pencil_utensil.lobby_boy.credentials.PasswordCredentials;
import pencil_utensil.lobby_boy.credentials.PasswordService;
import pencil_utensil.lobby_boy.exception.user.InvalidCredentialsException;
import pencil_utensil.lobby_boy.exception.user.UnavailableNameException;

@Service
public class UserService {

	private final PasswordService passwordService;
	private final UserRepository userRepository;

	UserService(UserRepository userRepository, PasswordService passwordService) {
		this.userRepository = userRepository;
		this.passwordService = passwordService;
	}

	@Transactional
	public User register(String name, String password) {
		try {
			// Perhaps should look into my other approach here. Ask someone?...
			PasswordCredentials credentials = passwordService.hash(password);
			User user = new User(name, credentials.hash(), credentials.salt());
			return userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UnavailableNameException();
		}
	}

	@Transactional(readOnly = true)
	public User authenticate(String name, String password) {
		User user = userRepository.getByName(name).orElseThrow(InvalidCredentialsException::new);

		if (!passwordService.match(user.getHash(), password, user.getSalt())) {
			throw new InvalidCredentialsException();
		}
		return user;
	}


	/*
	 * Do not treat this as authoritative, as it does not take into account any
	 * on-going registration transactions via register() method.
	 */
	@Transactional(readOnly = true)
	public boolean nameAvailable(String name) {
		return !userRepository.getByName(name).isPresent();
	}
}
