package io.cfp.auth.service;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Manage tokens
 */
@Service
public class TokenSrv {
    /** Number of hours after which a token expires */
    public static final long TOKEN_EXPIRATION = 12;

    @Value("${token.signing-key}")
    private String signingKey;


	/**
	 * Create a Token for a user
     * @param email Email of the token owner
     * @param isSuperAdmin True if the user is a super admin
     * @return Token value
     */
    public String create(String email, boolean isSuperAdmin) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .setExpiration(Date.from(Instant.now().plus(TOKEN_EXPIRATION, ChronoUnit.HOURS)))
                .signWith(SignatureAlgorithm.HS512, signingKey);

        if (isSuperAdmin) {
            builder.claim("superAdmin", true);
        }

        return builder.compact();
    }

	/**
     * Check if token is valid
     * @param token
     * @return
     */
    public boolean isValid(String token) {
        try {
            java.util.Date expiration = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody().getExpiration();
            if (expiration == null) return false;

            return expiration.toInstant().isAfter(Instant.now());
        } catch (ExpiredJwtException | UnsupportedJwtException | SignatureException | MalformedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Remove an active token
     * @param token Token to remove
     * @return Token removed or null
     */
    public String remove(String token) {
		if (token == null) return null;

        //TODO implement if revocation
        return null;
    }
}
