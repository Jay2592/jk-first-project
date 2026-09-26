package com.example.auth;

import com.example.security.JwtService;
import com.example.security.TokenStore;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenStore tokenStore;

    // access token lifetime in seconds (uses default from JwtService if null)
    private final long refreshTokenSeconds = 60L * 60L * 24L * 7L; // 7 days for refresh token

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, TokenStore tokenStore) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenStore = tokenStore;
    }

    public LoginResponse login(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        if (auth != null && auth.isAuthenticated()) {
            String accessToken = jwtService.generateToken(username);
            // create refresh token as JWT with longer expiry
            String refreshToken = jwtService.generateToken(username, refreshTokenSeconds);
            long refreshExpiry = Instant.now().toEpochMilli() + (refreshTokenSeconds * 1000);
            tokenStore.storeRefreshToken(refreshToken, refreshExpiry);
            return new LoginResponse(accessToken, refreshToken);
        }
        throw new RuntimeException("Authentication failed");
    }

    public String refresh(String refreshToken) {
        boolean ok = tokenStore.validateAndConsumeRefreshToken(refreshToken);
        if (!ok) throw new RuntimeException("Invalid or expired refresh token");
        // extract username and issue new access token
        String username = jwtService.extractUsername(refreshToken);
        return jwtService.generateToken(username);
    }

    public void logout(String accessToken, String refreshToken) {
        // invalidate refresh token
        if (refreshToken != null) tokenStore.invalidateRefreshToken(refreshToken);
        // blacklist access token until its natural expiry
        if (accessToken != null) {
            long exp = jwtService.extractExpirationMillis(accessToken);
            tokenStore.blacklistToken(accessToken, exp);
        }
    }
}
