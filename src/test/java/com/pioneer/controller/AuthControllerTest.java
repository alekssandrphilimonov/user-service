package com.pioneer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pioneer.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataSource dataSource;
    @Autowired
    PasswordEncoder passwordEncoder;

    private LoginRequest loginRequest;


    @BeforeEach
    void initTestData() throws Exception {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("data.sql"));
    }

    @BeforeEach
    void setup() {
        loginRequest = new LoginRequest();
        loginRequest.setLogin("test@mail.com");
        loginRequest.setPassword("password");
    }

    @Test
    void shouldReturnToken_whenLoginWithValidEmail() throws Exception {
        String response = mockMvc.perform(post("/auth/login-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).contains("token");
    }

    @Test
    void shouldReturnToken_whenLoginWithValidPhone() throws Exception {
        LoginRequest phoneRequest = new LoginRequest();
        phoneRequest.setLogin("79207865432");
        phoneRequest.setPassword("password");

        String response = mockMvc.perform(post("/auth/login-phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(phoneRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).contains("token");
    }

    @Test
    void shouldReturnCurrentUser_whenTokenIsValid() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setLogin("test@mail.com");
        login.setPassword("password");

        String token = mockMvc.perform(post("/auth/login-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = objectMapper.readTree(token).get("token").asText();

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.emails[0]").value("test@mail.com"))
                .andExpect(jsonPath("$.phones[0]").value("79207865432"));
    }

    @Test
    void checkPasswordEncoderStrength() {
        var encoded = passwordEncoder.encode("test");
        System.out.println(encoded); // просто для интереса
        assertThat(encoded).startsWith("$2a$12$");
    }

    @Test
    void shouldReturnUserByEmailFilter() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setLogin("test@mail.com");
        login.setPassword("password");

        String loginJson = objectMapper.writeValueAsString(login);

        MvcResult authResult = mockMvc.perform(post("/auth/login-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(authResult.getResponse().getContentAsString())
                .get("token").asText();

        mockMvc.perform(get("/api/users/search")
                        .param("email", "test@mail.com")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test User"))
                .andExpect(jsonPath("$.content[0].emails[0]").value("test@mail.com"));
    }
}