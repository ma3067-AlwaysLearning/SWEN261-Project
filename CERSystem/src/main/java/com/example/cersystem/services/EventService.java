package com.example.cersystem.services;


import com.example.cersystem.models.Event;
import com.example.cersystem.models.User;
import com.example.cersystem.repositories.EventRepository;
import com.example.cersystem.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private EventRepository eventRepository;
    private  UserRepository userRepository;

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> searchByName(String name) {
        return eventRepository.findByTitleContainingIgnoreCase(name);
    }

    public List<Event> searchByCategory(String category) {
        return eventRepository.findByCategory(category);
    }

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public void addToCollection(Long eventId, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();
        user.getEvents().add(event);
        userRepository.save(user);
    }

    public List<Event> getCollection(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getEvents();
    }

}
