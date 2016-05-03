package io.cfp.auth.dto;

/**
 * Data sent when a User successfully logged in
 */
public class LoginRes {

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
