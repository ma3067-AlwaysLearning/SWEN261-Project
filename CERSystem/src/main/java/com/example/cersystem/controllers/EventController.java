package com.example.cersystem.controllers;

import com.example.cersystem.models.Event;
import com.example.cersystem.services.EventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
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
    public String showEvents(Model model, Authentication auth, HttpServletRequest httpServletRequest) {
        model.addAttribute("events", eventService.getAll());
        model.addAttribute("isLoggedIn", auth != null && auth.isAuthenticated());
        if (auth != null && auth.isAuthenticated()) {
            model.addAttribute("userEmail", auth.getName());
        }

        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute("_csrf");
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        //noinspection SpringMvcViewInspection
        return "events";  // Mustache template: events.mustache
    }

    @PostMapping("/register/{eventId}")
    @ResponseBody
    public Map<String, Object> register(@PathVariable Long eventId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getName())) {
            return Map.of("success", false, "message", "Please log in first");
        }
        return eventService.registerForEvent(eventId, auth.getName());
    }
}