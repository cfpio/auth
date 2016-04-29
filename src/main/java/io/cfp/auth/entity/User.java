package io.cfp.auth.entity;

import javax.persistence.*;
import java.util.Set;

/**
 * User of the application
 */
@Entity(name = "users")
public class User {

	private String email;
	private String password;

	private Set<String> authorities;


	@Id
	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="user_authorities", joinColumns=@JoinColumn(name="email"))
	@Column(name="authority")
	public Set<String> getAuthorities() {
		return authorities;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(Set<String> authorities) {
		this.authorities = authorities;
	}
}
