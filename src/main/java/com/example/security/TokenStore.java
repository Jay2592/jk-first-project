package com.example.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Simple in-memory token store for refresh tokens and blacklisted access tokens.
@Component
public class TokenStore {

    // refreshToken -> username:expiryMillis
    private final Map<String, Long> refreshTokens = new ConcurrentHashMap<>();
    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void storeRefreshToken(String refreshToken, long expiryEpochMillis) {
        refreshTokens.put(refreshToken, expiryEpochMillis);
    }

    public boolean validateAndConsumeRefreshToken(String refreshToken) {
        Long exp = refreshTokens.get(refreshToken);
        if (exp == null) return false;
        if (Instant.now().toEpochMilli() > exp) {
            refreshTokens.remove(refreshToken);
            return false;
        }
        // consume: remove on use
        refreshTokens.remove(refreshToken);
        return true;
    }

    public void invalidateRefreshToken(String refreshToken) {
        refreshTokens.remove(refreshToken);
    }

    public void blacklistToken(String token, long expiryEpochMillis) {
        blacklistedTokens.put(token, expiryEpochMillis);
    }

    public boolean isBlacklisted(String token) {
        Long exp = blacklistedTokens.get(token);
        if (exp == null) return false;
        if (Instant.now().toEpochMilli() > exp) {
            blacklistedTokens.remove(token);
            return false;
        }
        return true;
    }
}
