package com.example.cersystem.services;

import com.example.cersystem.models.User;
import com.example.cersystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserDetailsServTest {

    @Autowired
    private UserDetailsServ userDetailsServ;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private User testUser;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        testUser.setEmail("test@rit.edu");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setRole("USER");

        when(userRepository.findByEmail("test@rit.edu"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("anon@exists.com"))
                .thenReturn(Optional.empty());
    }

    @Test
    void validEmail_thenDetailsLoaded() {
        UserDetails userDetails = userDetailsServ
                .loadUserByUsername("test@rit.edu");

        assertNotNull(userDetails);
        assertEquals("test@rit.edu", userDetails.getUsername());
    }

    @Test
    void validEmail_isPasswordHashed() {
        UserDetails userDetails = userDetailsServ
                .loadUserByUsername("test@rit.edu");

        assertTrue(passwordEncoder.matches("password", userDetails.getPassword()));
    }

    @Test
    void unknownEmail_throwsException() {
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsServ.loadUserByUsername("anon@exist.com"));
    }
}
