package pencil_utensil.lobby_boy.user;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 50)
	private String name;

	@Column(nullable = false)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private UserRole role;
	@Column(nullable = false)
	private String hash;
	@Column(nullable = false, length = 10)
	private String salt;

	protected User() {};

	public User(String name, String hash, String salt) {
		this.name = name;
		this.hash = hash;
		this.salt = salt;
	}

	public static User getDefaultUser() { return new User("", "", ""); }

	public Integer getId() { return id; }

	public String getName() { return name; }

	public String getHash() { return hash; }

	public String getSalt() { return salt; }

	public UserRole getRole() { return role; }

	public void setName(String name) { this.name = name; }

	public void setHash(String hash) { this.hash = hash; }

	public void setSalt(String salt) { this.salt = salt; }

	public void setRole(UserRole role) { this.role = role; }
}
