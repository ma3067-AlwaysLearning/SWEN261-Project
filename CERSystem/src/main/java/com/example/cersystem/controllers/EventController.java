package com.example.cersystem.controllers;

import com.example.cersystem.dto.EventRequest;
import com.example.cersystem.models.Event;
import com.example.cersystem.services.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
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
        model.addAttribute("isStudent", auth != null && auth.isAuthenticated()
                && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            model.addAttribute("userEmail", auth.getName());
        }

        CsrfToken csrfToken = (CsrfToken) httpServletRequest.getAttribute("_csrf");
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        return "events";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<?> getEventsApi(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String category,
                                          @RequestParam(required = false) String location,
                                          @RequestParam(required = false) String organizer,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "End date cannot be earlier than start date"
            ));
        }

        List<EventSummaryResponse> events = eventService.searchAndFilter(keyword, category, location, organizer, startDate, endDate)
                .stream()
                .map(EventSummaryResponse::from)
                .toList();

        return ResponseEntity.ok(Map.of(
                "count", events.size(),
                "events", events
        ));
    }

    @GetMapping("/my/api")
    @ResponseBody
    public ResponseEntity<?> getMyEventsApi(Authentication auth) {

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Please log in first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        List<Event> registeredEvents = eventService.getRegisteredEvents(auth.getName());
        List<Map<String, Object>> events = new ArrayList<>();

        for (Event event : registeredEvents) {
            Map<String, Object> eventData = new HashMap<>();

            eventData.put("eventId", event.getEventId());
            eventData.put("title", event.getTitle());
            eventData.put("description", event.getDescription());
            eventData.put("scheduledDate", event.getScheduledDate());
            eventData.put("startTime", event.getStartTime());
            eventData.put("endTime", event.getEndTime());
            eventData.put("category", event.getCategory());
            eventData.put("location", event.getLocation());
            eventData.put("status", event.getStatus());

            if (event.getOrganizer() != null) {
                eventData.put("organizerName", event.getOrganizer().getName());
            } else {
                eventData.put("organizerName", "");
            }

            events.add(eventData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("count", events.size());
        response.put("events", events);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/{eventId}")
    @ResponseBody
    public Map<String, Object> register(@PathVariable Long eventId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Map.of("success", false, "message", "Please log in first");
        }
        return eventService.registerForEvent(eventId, auth.getName());
    }

    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventRequest request,
                                         BindingResult bindingResult,
                                         Authentication auth) {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors()
                    .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "You must be logged in"));
        }

        Event created = eventService.createEvent(request, auth.getName());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Event created successfully",
                "eventId", created.getEventId()
        ));
    }

    @PutMapping("/{eventId}")
    @ResponseBody
    public Map<String, Object> updateEvent(@PathVariable Long eventId,
                                           @RequestBody Event updatedEvent,
                                           Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Map.of("success", false, "message", "Please log in first");
        }
        return eventService.editEvent(eventId, updatedEvent, auth.getName());
    }

    @PostMapping("/{eventId}/cancel")
    @ResponseBody
    public Map<String, Object> cancelEvent(@PathVariable Long eventId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Map.of("success", false, "message", "Please log in first");
        }
        return eventService.cancelEvent(eventId, auth.getName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    private boolean hasFilters(String keyword,
                               String category,
                               String location,
                               String organizer,
                               LocalDate startDate,
                               LocalDate endDate) {
        return hasText(keyword) || hasText(category) || hasText(location) || hasText(organizer)
                || startDate != null || endDate != null;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    public record EventSummaryResponse(Long eventId,
                                       String title,
                                       String description,
                                       LocalDate scheduledDate,
                                       String category,
                                       String location,
                                       String organizerName,
                                       String status) {
        static EventSummaryResponse from(Event event) {
            return new EventSummaryResponse(
                    event.getEventId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getScheduledDate(),
                    event.getCategory(),
                    event.getLocation(),
                    event.getOrganizer() != null ? event.getOrganizer().getName() : null,
                    event.getStatus()
            );
        }
    }
}