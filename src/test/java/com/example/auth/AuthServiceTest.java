package com.example.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class AuthServiceTest {

    @Test
    void loginSuccessReturnsToken() {
        AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.isAuthenticated()).thenReturn(true);
        Mockito.when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        // mock JwtService to return a deterministic token
        com.example.security.JwtService jwtService = Mockito.mock(com.example.security.JwtService.class);
        com.example.security.TokenStore tokenStore = Mockito.mock(com.example.security.TokenStore.class);
        Mockito.when(jwtService.generateToken("user")).thenReturn("jwt-token-abc123");
        Mockito.when(jwtService.generateToken("user", 604800L)).thenReturn("refresh-token-xyz");

        AuthService service = new AuthService(authManager, jwtService, tokenStore);
        LoginResponse resp = service.login("user", "password");
        assertNotNull(resp);
        assertEquals("jwt-token-abc123", resp.getToken());
        assertEquals("refresh-token-xyz", resp.getRefreshToken());
    }

    @Test
    void loginFailureThrows() {
        AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);
        Mockito.when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("bad creds"));

        com.example.security.JwtService jwtService = Mockito.mock(com.example.security.JwtService.class);
        com.example.security.TokenStore tokenStore = Mockito.mock(com.example.security.TokenStore.class);
        AuthService service = new AuthService(authManager, jwtService, tokenStore);
        assertThrows(RuntimeException.class, () -> service.login("user", "wrong"));
    }
}
