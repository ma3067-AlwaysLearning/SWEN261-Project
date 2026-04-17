package com.example.cersystem.controllers;

import com.example.cersystem.models.Event;
import com.example.cersystem.services.EventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

<<<<<<< Updated upstream
=======
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    public String showEvents(Model model, Authentication auth, HttpServletRequest httpServletRequest) {
        model.addAttribute("events", eventService.getAll());
        model.addAttribute("isLoggedIn", auth != null && auth.isAuthenticated());
        if (auth != null && auth.isAuthenticated()) {
=======
    public String showEvents(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) String organizer,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                             Model model,
                             Authentication auth,
                             HttpServletRequest httpServletRequest) {
        List<Event> events = eventService.searchAndFilter(keyword, category, location, organizer, startDate, endDate);

        model.addAttribute("events", events);
        model.addAttribute("keyword", valueOrEmpty(keyword));
        model.addAttribute("category", valueOrEmpty(category));
        model.addAttribute("location", valueOrEmpty(location));
        model.addAttribute("organizer", valueOrEmpty(organizer));
        model.addAttribute("startDate", startDate != null ? startDate.toString() : "");
        model.addAttribute("endDate", endDate != null ? endDate.toString() : "");
        model.addAttribute("hasFilters", hasFilters(keyword, category, location, organizer, startDate, endDate));
        model.addAttribute("resultCount", events.size());
        model.addAttribute("isLoggedIn", auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName()));
        model.addAttribute("isStudent", auth != null && auth.isAuthenticated() && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));


        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
>>>>>>> Stashed changes
            model.addAttribute("userEmail", auth.getName());
        }

        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute("_csrf");
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        //noinspection SpringMvcViewInspection
        return "events";  // Mustache template: events.mustache
    }

    // This API returns the events that the logged-in student registered for
    @GetMapping("/my/api")
    @ResponseBody
    public ResponseEntity<?> getMyEventsApi(Authentication auth) {

        // Check if the user is logged in
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Please log in first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Get the logged-in user's registered events from the service
        List<Event> registeredEvents = eventService.getRegisteredEvents(auth.getName());

        // This list will hold the event data we want to send to Angular
        List<Map<String, Object>> events = new ArrayList<>();

        // Loop through each registered event
        for (Event event : registeredEvents) {
            Map<String, Object> eventData = new HashMap<>();

            // Add event id
            eventData.put("eventId", event.getEventId());

            // Add event title
            eventData.put("title", event.getTitle());

            // Add event description
            eventData.put("description", event.getDescription());

            // Add the event date
            eventData.put("scheduledDate", event.getScheduledDate());

            // Add the event start time
            eventData.put("startTime", event.getStartTime());

            // Add the event end time
            eventData.put("endTime", event.getEndTime());

            // Add the event category
            eventData.put("category", event.getCategory());

            // Add the event location
            eventData.put("location", event.getLocation());

            // Add the event status
            eventData.put("status", event.getStatus());

            // Add organizer name safely
            if (event.getOrganizer() != null) {
                eventData.put("organizerName", event.getOrganizer().getName());
            } else {
                eventData.put("organizerName", "");
            }

            // Add this event's data to the final list
            events.add(eventData);
        }

        // Create the final response for Angular
        Map<String, Object> response = new HashMap<>();
        response.put("count", events.size());
        response.put("events", events);

        return ResponseEntity.ok(response);
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