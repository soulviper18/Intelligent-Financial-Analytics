package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("unused")
public class AuthFlowIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Common credentials for tests that just need "a user"
    private final String SETUP_EMAIL = "setup_user@example.com";
    private final String PASSWORD = "password123";

    @BeforeEach
    void setup() throws Exception {
        // We use the REAL signup API to create the user.
        // Since our Test Base is @Transactional, this rolls back after every test,
        // so we don't need to worry about "User already exists" errors.
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Setup User",
                      "email": "%s",
                      "password": "%s"
                    }
                """.formatted(SETUP_EMAIL, PASSWORD)));
    }

    @Test
    void signup_login_and_access_protected_endpoint() throws Exception {
        // This test creates its OWN unique user to verify the full flow independent of setup()
        String uniqueEmail = "flow_test@example.com";

        // 1. Signup
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "Flow User",
                      "email": "%s",
                      "password": "%s"
                    }
                """.formatted(uniqueEmail, PASSWORD)))
                .andExpect(status().isOk());

        // 2. Login
        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "%s",
                      "password": "%s"
                    }
                """.formatted(uniqueEmail, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String accessToken = getToken(loginResponse, "accessToken");

        // 3. Access Protected API
        mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail));
    }

    @Test
    void refresh_token_cannot_access_api() throws Exception {
        // Login using the SETUP user
        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "%s",
                      "password": "%s"
                    }
                """.formatted(SETUP_EMAIL, PASSWORD)))
                .andDo(print()) // Debug print if it fails
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Extract Refresh Token
        String refreshToken = getToken(loginResponse, "refreshToken");

        // Try to access API using Refresh Token (Should Fail)
        mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized()); // Or .isForbidden()
    }

    @Test
    void user_cannot_access_admin_endpoint() throws Exception {
        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "%s",
                      "password": "%s"
                    }
                """.formatted(SETUP_EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String accessToken = getToken(loginResponse, "accessToken");

        mockMvc.perform(get("/admin/users")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void blacklisted_token_is_rejected() throws Exception {
        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "%s",
                      "password": "%s"
                    }
                """.formatted(SETUP_EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String accessToken = getToken(loginResponse, "accessToken");

        // Logout
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // Token should now be invalid
        mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized());
    }

    // Helper method to safely extract tokens
    private String getToken(String json, String key) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        if (root.has(key)) {
            return root.get(key).asText();
        }
        throw new RuntimeException("Token not found in response: " + json);
    }
}
