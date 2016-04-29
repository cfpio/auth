package io.cfp.auth.service;

import io.cfp.auth.dto.Token;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manage tokens
 */
@Service
public class TokenSrv {
    /** Number of minutes after which a token expires */
    public static final long TOKEN_EXPIRATION = 30;

    /** Map of active tokens by token id */
    private Map<String, Token> activeTokens = new HashMap<>();


    /**
     * Create a token and add into active token
     * @param email Email of the token owner
     * @param permissions Permissions of the token owner
     * @return New token
     */
    public Token create(String email, Set<String> permissions) {
        Token token = new Token(TOKEN_EXPIRATION, email, permissions);
        activeTokens.put(token.getValue(), token);

        return token;
    }

    /**
     * Retrieve an active token
     * @param token Token to retrieve
     * @return Token if found or null
     */
    public Token get(String token) {
		if (token == null) return null;

        Token resToken = activeTokens.get(token);
        if (resToken == null) return null;

        if (Instant.now().isAfter(resToken.getValidUntil())) {
            remove(token);
            return null;
        }

        resToken.updateValidUntil(TOKEN_EXPIRATION);
        return resToken;
    }

    /**
     * Remove an active token
     * @param token Token to remove
     * @return Token removed or null
     */
    public Token remove(String token) {
		if (token == null) return null;

        return activeTokens.remove(token);
    }
}
