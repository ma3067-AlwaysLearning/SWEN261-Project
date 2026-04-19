package com.example.cersystem.controllers;

import com.example.cersystem.models.Event;
import com.example.cersystem.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventApiController {

    private final EventService eventService;

    public EventApiController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/organizer")
    public ResponseEntity<?> getOrganizerEvents(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in first"));
        }

        List<Event> organizerEvents = eventService.getOrganizerEvents(auth.getName());

        List<Map<String, Object>> events = organizerEvents.stream().map(event -> {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventId", event.getEventId());
            eventData.put("title", event.getTitle());
            eventData.put("description", event.getDescription());
            eventData.put("scheduledDate", event.getScheduledDate());
            eventData.put("startTime", event.getStartTime());
            eventData.put("endTime", event.getEndTime());
            eventData.put("category", event.getCategory());
            eventData.put("location", event.getLocation());
            eventData.put("capacity", event.getCapacity());
            eventData.put("status", event.getStatus());
            eventData.put("organizerName", event.getOrganizer() != null ? event.getOrganizer().getName() : "");
            return eventData;
        }).toList();

        return ResponseEntity.ok(Map.of(
                "count", events.size(),
                "events", events
        ));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId,
                                         @RequestBody Event updatedEvent,
                                         Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Please log in first"));
        }

        return ResponseEntity.ok(eventService.editEvent(eventId, updatedEvent, auth.getName()));
    }

    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<?> cancelEvent(@PathVariable Long eventId, Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Please log in first"));
        }

        return ResponseEntity.ok(eventService.cancelEvent(eventId, auth.getName()));
    }
}