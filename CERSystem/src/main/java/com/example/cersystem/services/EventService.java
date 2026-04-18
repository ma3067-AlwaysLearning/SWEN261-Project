package com.example.cersystem.services;

import com.example.cersystem.models.Event;
import com.example.cersystem.models.User;
import com.example.cersystem.repositories.EventRepository;
import com.example.cersystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }


    // US-09: Edit Event
    public Map<String, Object> editEvent(Long eventId, Event updatedEvent, String organizerEmail) {
        Map<String, Object> result = new HashMap<>();

        User organizer = userRepository.findByEmail(organizerEmail).orElse(null);
        if (organizer == null) {
            result.put("success", false);
            result.put("message", "You must be logged in");
            return result;
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            result.put("success", false);
            result.put("message", "Event does not exist");
            return result;
        }

        // Only owner can edit
        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            result.put("success", false);
            result.put("message", "You can only edit your own events");
            return result;
        }

        // Update fields
        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setScheduledDate(updatedEvent.getScheduledDate());
        event.setCategory(updatedEvent.getCategory());
        event.setLocation(updatedEvent.getLocation());
        event.setRegistrationStart(updatedEvent.getRegistrationStart());
        event.setRegistrationEnd(updatedEvent.getRegistrationEnd());
        event.setStartTime(updatedEvent.getStartTime());
        event.setEndTime(updatedEvent.getEndTime());

        eventRepository.save(event);

        result.put("success", true);
        result.put("message", "Event updated successfully");
        result.put("event", event);
        return result;
    }

    // === US-09: Cancel Event ===
    public Map<String, Object> cancelEvent(Long eventId, String organizerEmail) {
        Map<String, Object> result = new HashMap<>();

        User organizer = userRepository.findByEmail(organizerEmail).orElse(null);
        if (organizer == null) {
            result.put("success", false);
            result.put("message", "You must be logged in");
            return result;
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            result.put("success", false);
            result.put("message", "Event does not exist");
            return result;
        }

        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            result.put("success", false);
            result.put("message", "You can only cancel your own events");
            return result;
        }

        event.setStatus("CANCELLED");
        eventRepository.save(event);

        result.put("success", true);
        result.put("message", "Event has been cancelled");
        return result;
    }
    public Event save(Event event) { return eventRepository.save(event); }
    public List<Event> searchByName(String name) { return eventRepository.findByTitleContainingIgnoreCase(name); }
    public List<Event> searchByCategory(String category) { return eventRepository.findByCategoryIgnoreCase(category); }
    public List<Event> getAll() { return eventRepository.findAll(); }
    public Map<String, Object> registerForEvent(Long eventId, String email) {
        Map<String, Object> result = new HashMap<>();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            result.put("success", false);
            result.put("message", "You must be logged in");
            return result;
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            result.put("success", false);
            result.put("message", "Event does not exist");
            return result;
        }

        if (user.getEvents().stream().anyMatch(e -> e.getEventId().equals(eventId))) {
            result.put("success", false);
            result.put("message", "You are already registered for this event");
            return result;
        }

        user.getEvents().add(event);
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "Registration successful! Your spot is confirmed for " + event.getTitle());
        return result;
    }
}