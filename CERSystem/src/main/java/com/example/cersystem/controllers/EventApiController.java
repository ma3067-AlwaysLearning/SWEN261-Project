package com.example.cersystem.controllers;

import com.example.cersystem.models.Event;
import com.example.cersystem.services.EventServiceAngular;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventApiController {

    private final EventServiceAngular eventService;

    public EventApiController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        List<EventResponse> results = eventService.searchAndFilter(keyword, category, location, date)
                .stream()
                .map(EventResponse::from)
                .toList();

        return ResponseEntity.ok(results);
    }

    public record EventResponse(
            Long eventId,
            String title,
            String description,
            LocalDate scheduledDate,
            String category,
            String location,
            String status
    ) {
        public static EventResponse from(Event event) {
            return new EventResponse(
                    event.getEventId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getScheduledDate(),
                    event.getCategory(),
                    event.getLocation(),
                    event.getStatus()
            );
        }
    }
}