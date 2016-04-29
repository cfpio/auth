package io.cfp.auth.dto;

import java.util.Set;

/**
 * Data sent when a User successfully logged in
 */
public class LoginRes {

	private String token;

	private Set<String> permissions;


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

}
