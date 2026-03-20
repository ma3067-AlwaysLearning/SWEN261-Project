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

    public Event save(Event event) { return eventRepository.save(event); }
    public List<Event> searchByName(String name) { return eventRepository.findByTitleContainingIgnoreCase(name); }
    public List<Event> searchByCategory(String category) { return eventRepository.findByCategory(category); }
    public List<Event> getAll() { return eventRepository.findAll(); }
    public List<Event> getCollection(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getEvents();
    }

    // US-06
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

        // duplicate check
        if (user.getEvents().stream().anyMatch(e -> e.getEventId().equals(eventId))) {
            result.put("success", false);
            result.put("message", "You are already registered for this event");
            return result;
        }

        // full capacity adding int capacity field to Event still could be needed
        // To add capacity column (currentCount >= capacity) → "Event is full"
        // result.put("success", false); result.put("message", "Event is full"); return result;

        // register
        user.getEvents().add(event);
        userRepository.save(user);

        result.put("success", true);
        result.put("message", "Registration successful! Your spot is confirmed for " + event.getTitle());
        return result;
    }
}