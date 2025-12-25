package com.example.demo.integration;

import com.example.demo.dto.auth.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testLoginEndpoint() throws Exception {

        LoginRequest req = new LoginRequest();
        req.setEmail("testuser@gmail.com");
        req.setPassword("test123");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request =
                new HttpEntity<>(mapper.writeValueAsString(req), headers);

        ResponseEntity<String> response =
                rest.postForEntity("/auth/login", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
