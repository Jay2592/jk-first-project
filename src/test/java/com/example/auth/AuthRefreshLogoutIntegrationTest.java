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
class AuthRefreshLogoutIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void refreshAndLogoutFlow() {
        String loginUrl = "http://localhost:" + port + "/api/login";
        LoginRequest req = new LoginRequest("user", "password");
        ResponseEntity<LoginResponse> resp = restTemplate.postForEntity(loginUrl, req, LoginResponse.class);
        assertEquals(200, resp.getStatusCodeValue());
        assertNotNull(resp.getBody());
        String access = resp.getBody().getToken();
        String refresh = resp.getBody().getRefreshToken();
        assertNotNull(access);
        assertNotNull(refresh);

        // call protected endpoint with access token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(access);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String helloUrl = "http://localhost:" + port + "/api/hello";
        ResponseEntity<String> helloResp = restTemplate.exchange(helloUrl, org.springframework.http.HttpMethod.GET, entity, String.class);
        assertEquals(200, helloResp.getStatusCodeValue());

        // refresh
        String refreshUrl = "http://localhost:" + port + "/api/refresh";
        RefreshRequest rr = new RefreshRequest(refresh);
        ResponseEntity<LoginResponse> refreshResp = restTemplate.postForEntity(refreshUrl, rr, LoginResponse.class);
        assertEquals(200, refreshResp.getStatusCodeValue());
        assertNotNull(refreshResp.getBody());
        assertNotNull(refreshResp.getBody().getToken());

        // logout: invalidate both
        String logoutUrl = "http://localhost:" + port + "/api/logout";
        LogoutRequest lr = new LogoutRequest(access, refresh);
        ResponseEntity<Void> logoutResp = restTemplate.postForEntity(logoutUrl, lr, Void.class);
        assertEquals(204, logoutResp.getStatusCodeValue());

        // subsequent call with the old access token should fail (unauthorized or forbidden)
        ResponseEntity<String> helloAfter = restTemplate.exchange(helloUrl, org.springframework.http.HttpMethod.GET, entity, String.class);
        assertTrue(helloAfter.getStatusCodeValue() == 401 || helloAfter.getStatusCodeValue() == 403);
    }
}
