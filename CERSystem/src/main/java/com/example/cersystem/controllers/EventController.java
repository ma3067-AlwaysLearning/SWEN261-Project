package com.example.cersystem.controllers;

import com.example.cersystem.models.Event;
import com.example.cersystem.services.EventService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    // Constructor injection
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String showEvents(Model model, Authentication auth) {
        model.addAttribute("events", eventService.getAll());
        model.addAttribute("isLoggedIn", auth != null && auth.isAuthenticated());
        if (auth != null && auth.isAuthenticated()) {
            model.addAttribute("userEmail", auth.getName());
        }
        //noinspection SpringMvcViewInspection
        return "events";  // Mustache template: events.mustache
    }

    @PostMapping("/register/{eventId}")
    @ResponseBody
    public Map<String, Object> register(@PathVariable Long eventId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return Map.of("success", false, "message", "Please log in first");
        }
        return eventService.registerForEvent(eventId, auth.getName());
    }
}