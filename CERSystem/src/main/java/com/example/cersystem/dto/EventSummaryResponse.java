package com.example.cersystem.dto;

import com.example.cersystem.models.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record EventSummaryResponse(Long eventId,
                                   String title,
                                   String description,
                                   LocalDate scheduledDate,
                                   String startTime,
                                   String endTime,
                                   String registrationStart,
                                   String registrationEnd,
                                   String category,
                                   String location,
                                   String organizerName,
                                   String status,
                                   int capacity,
                                   int spotLeft) {

    private static final DateTimeFormatter timeOnly = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter dateTime = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    public static EventSummaryResponse from(Event event, int registeredCount) {
        int spotLeft = event.getCapacity() - registeredCount;
        return new EventSummaryResponse(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getScheduledDate(),
                event.getStartTime().format(timeOnly),
                event.getEndTime().format(timeOnly),
                event.getRegistrationStart().format(dateTime),
                event.getRegistrationEnd().format(dateTime),
                event.getCategory(),
                event.getLocation(),
                event.getOrganizer() != null ? event.getOrganizer().getName() : null,
                event.getStatus(),
                event.getCapacity(),
                spotLeft

        );
    }
}