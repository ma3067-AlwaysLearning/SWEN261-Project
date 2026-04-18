package com.example.cersystem.controllers;

import com.example.cersystem.models.Event;
import com.example.cersystem.models.Role;
import com.example.cersystem.models.User;
import com.example.cersystem.services.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    private Event event;

    @BeforeEach
    void setUp() {
        User organizer = new User();
        organizer.setId(1L);
        organizer.setName("System Organizer");
        organizer.setEmail("organizer@rit.edu");
        organizer.setRole(Role.ORGANIZER);
        organizer.setUniversity_id("ORG000001");

        event = new Event(
                organizer,
                LocalDateTime.of(2026, 4, 1, 12, 0),
                LocalDateTime.of(2026, 4, 1, 10, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59),
                LocalDateTime.of(2026, 3, 15, 8, 0),
                "Building H, Room 101",
                "Workshop",
                "Hands-on introduction to Spring Boot and REST APIs.",
                LocalDate.of(2026, 4, 1),
                "Spring Boot Workshop",
                10
        );
        event.setEventId(1L);
        event.setStatus("SCHEDULED");

        when(eventService.searchAndFilter(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(event));
        when(eventService.registerForEvent(eq(1L), anyString()))
                .thenReturn(Map.of("success", true, "message", "Registration successful"));
    }

    @Test
    void eventsPageLoadsWithFilters() throws Exception {
        mockMvc.perform(get("/events").param("keyword", "workshop"))
                .andExpect(status().isOk())
                .andExpect(view().name("events"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attribute("keyword", "workshop"));
    }

    @Test
    void eventsApiReturnsFilteredEvents() throws Exception {
        mockMvc.perform(get("/events/api").param("category", "Workshop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.events[0].title").value("Spring Boot Workshop"));
    }

    @Test
    void eventsApiRejectsInvalidDateRange() throws Exception {
        mockMvc.perform(get("/events/api")
                        .param("startDate", "2026-04-10")
                        .param("endDate", "2026-04-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("End date cannot be earlier than start date"));
    }

    @Test
    void registerRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/events/register/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void registerWorksForAuthenticatedStudent() throws Exception {
        mockMvc.perform(post("/events/register/1")
                        .with(user("student@rit.edu").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
