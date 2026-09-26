package com.example.auth;

import com.example.auth.LoginRequest;
import com.example.auth.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
            LoginResponse resp = authService.login(req.getUsername(), req.getPassword());
            return ResponseEntity.ok(resp);
    }

        @PostMapping("/refresh")
        public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest req) {
            String token = authService.refresh(req.getRefreshToken());
            return ResponseEntity.ok(new LoginResponse(token, null));
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logout(@RequestBody LogoutRequest req) {
            authService.logout(req.getAccessToken(), req.getRefreshToken());
            return ResponseEntity.noContent().build();
        }
}
