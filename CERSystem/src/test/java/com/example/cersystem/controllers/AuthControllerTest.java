package com.example.cersystem.controllers;

import com.example.cersystem.models.User;
import com.example.cersystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private User testUser;

    @BeforeEach
    void testSetUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@rit.edu");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole("USER");
        testUser.setUniversity_id("000000000");


        when(userRepository.findByEmail("test@rit.edu"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("anon@exists.com"))
                .thenReturn(Optional.empty());
    }

    @Test
    void validCredentials_Test() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "test@rit.edu")
                        .param("password", "password")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void invalidPassword_Test() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "test@rit.edu")
                        .param("password", "wrongpassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void unknownEmail_Test() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "anon@exists.com")
                        .param("password", "password123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void status200_loginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void errorAttributeCaptured_Test() throws Exception {
        mockMvc.perform(get("/login")
                        .param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void loggedOutAttributeCaptured_Test() throws Exception {
        mockMvc.perform(get("/login")
                        .param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("logout"));
    }

}
