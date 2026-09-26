package com.example.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.example.app.Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthProtectedEndpointTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void protectedEndpointRequiresJwt() {
        String helloUrl = "http://localhost:" + port + "/api/hello";

        // call without token -> should be 401
        ResponseEntity<String> respNoAuth = restTemplate.getForEntity(helloUrl, String.class);
        assertTrue(respNoAuth.getStatusCodeValue() == 401 || respNoAuth.getStatusCodeValue() == 403,
                "Expected 401/403 when no auth, got " + respNoAuth.getStatusCodeValue());

        // login to obtain JWT
        String loginUrl = "http://localhost:" + port + "/api/login";
        LoginRequest loginReq = new LoginRequest("user", "password");
        ResponseEntity<LoginResponse> loginResp = restTemplate.postForEntity(loginUrl, loginReq, LoginResponse.class);
        assertEquals(200, loginResp.getStatusCodeValue());
        assertNotNull(loginResp.getBody());
        String jwt = loginResp.getBody().getToken();
        assertNotNull(jwt);

        // call with Bearer token -> should be 200
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> respAuth = restTemplate.exchange(helloUrl, org.springframework.http.HttpMethod.GET, entity, String.class);
        assertEquals(200, respAuth.getStatusCodeValue());
        assertTrue(respAuth.getBody().contains("Hello"));
    }
}
