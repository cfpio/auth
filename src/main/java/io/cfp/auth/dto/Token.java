package io.cfp.auth.dto;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Active user token
 */
public class Token {

	private final String value;

	private Instant validUntil;

	private String login;

	private Set<String> permissions;

	/**
	 * Create a user token
	 * @param minutesValidity Number of minutes the token will remains valid if not updated
	 */
	public Token(long minutesValidity, String login, Set<String> permissions) {
		this.login = login;
		this.permissions = permissions;
		this.value = RandomStringUtils.randomAlphanumeric(32);
		updateValidUntil(minutesValidity);
	}

	/**
	 * Update validity date
	 * @param nbMinutes Number of minutes the token will remains valid if not updated
	 */
	public void updateValidUntil(long nbMinutes) {
		validUntil = Instant.now().plus(nbMinutes, ChronoUnit.MINUTES);
	}

	public String getValue() {
		return value;
	}

	public Instant getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Instant validUntil) {
		this.validUntil = validUntil;
	}

	public String getLogin() {
		return login;
	}

	public Set<String> getPermissions() {
		return permissions;
	}
}
