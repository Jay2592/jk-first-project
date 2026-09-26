package com.example.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.example.app.Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void loginEndpointReturnsToken() {
        String url = "http://localhost:" + port + "/api/login";
        LoginRequest req = new LoginRequest("user", "password");
        ResponseEntity<LoginResponse> resp = restTemplate.postForEntity(url, req, LoginResponse.class);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        assertNotNull(resp.getBody().getToken());
        assertFalse(resp.getBody().getToken().isEmpty());
        assertNotNull(resp.getBody().getRefreshToken());
        assertFalse(resp.getBody().getRefreshToken().isEmpty());
    }
}
