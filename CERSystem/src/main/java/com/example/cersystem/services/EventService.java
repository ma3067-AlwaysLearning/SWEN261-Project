package com.example.cersystem.services;

import com.example.cersystem.dto.EventRequest;
import com.example.cersystem.models.Event;
import com.example.cersystem.models.User;
import com.example.cersystem.repositories.EventRepository;
import com.example.cersystem.repositories.UserRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> searchByName(String name) {
        return eventRepository.findByTitleContainingIgnoreCase(name);
    }

    public List<Event> searchByCategory(String category) {
        return eventRepository.findByCategoryIgnoreCase(category);
    }

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public List<Event> searchAndFilter(String keyword,
                                       String category,
                                       String location,
                                       String organizer,
                                       LocalDate startDate,
                                       LocalDate endDate) {
        Specification<Event> specification = (root, query, cb) ->
                cb.or(
                        cb.isNull(root.get("status")),
                        cb.notEqual(cb.upper(root.get("status")), "CANCELLED")
                );

        if (hasText(keyword)) {
            String likeValue = wrapLike(keyword);
            specification = specification.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), likeValue),
                    cb.like(cb.lower(root.get("description")), likeValue),
                    cb.like(cb.lower(root.get("category")), likeValue),
                    cb.like(cb.lower(root.get("location")), likeValue)
            ));
        }

        if (hasText(category)) {
            specification = specification.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category")), category.trim().toLowerCase()));
        }

        if (hasText(location)) {
            String likeValue = wrapLike(location);
            specification = specification.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("location")), likeValue));
        }

        if (hasText(organizer)) {
            String likeValue = wrapLike(organizer);
            specification = specification.and((root, query, cb) -> {
                var organizerJoin = root.join("organizer", JoinType.INNER);
                return cb.or(
                        cb.like(cb.lower(organizerJoin.get("name")), likeValue),
                        cb.like(cb.lower(organizerJoin.get("email")), likeValue)
                );
            });
        }

        if (startDate != null) {
            specification = specification.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("scheduledDate"), startDate));
        }

        if (endDate != null) {
            specification = specification.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("scheduledDate"), endDate));
        }

        return eventRepository.findAll(specification);
    }

    public List<Event> getRegisteredEvents(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getEvents();
    }

    public List<Event> getOrganizerEvents(String email) {
        return eventRepository.findByOrganizer_EmailIgnoreCase(email)
                .stream()
                .filter(event -> event.getStatus() == null || !"CANCELLED".equalsIgnoreCase(event.getStatus()))
                .toList();
    }

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

        if (event.getOrganizer() == null || !event.getOrganizer().getEmail().equalsIgnoreCase(organizerEmail)) {
            result.put("success", false);
            result.put("message", "You can only edit your own events");
            return result;
        }

        if (updatedEvent.getTitle() != null) {
            event.setTitle(updatedEvent.getTitle());
        }
        if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getScheduledDate() != null) {
            event.setScheduledDate(updatedEvent.getScheduledDate());
        }
        if (updatedEvent.getCategory() != null) {
            event.setCategory(updatedEvent.getCategory());
        }
        if (updatedEvent.getLocation() != null) {
            event.setLocation(updatedEvent.getLocation());
        }
        if (updatedEvent.getRegistrationStart() != null) {
            event.setRegistrationStart(updatedEvent.getRegistrationStart());
        }
        if (updatedEvent.getRegistrationEnd() != null) {
            event.setRegistrationEnd(updatedEvent.getRegistrationEnd());
        }
        if (updatedEvent.getStartTime() != null) {
            event.setStartTime(updatedEvent.getStartTime());
        }
        if (updatedEvent.getEndTime() != null) {
            event.setEndTime(updatedEvent.getEndTime());
        }

        eventRepository.save(event);

        result.put("success", true);
        result.put("message", "Event updated successfully");
        result.put("eventId", event.getEventId());
        return result;
    }

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

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String wrapLike(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }

    public Event createEvent(EventRequest request, String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        Event event = new Event(
                organizer,
                request.getEndTime(),
                request.getStartTime(),
                request.getRegistrationEnd(),
                request.getRegistrationStart(),
                request.getLocation(),
                request.getCategory(),
                request.getDescription(),
                request.getScheduledDate(),
                request.getTitle(),
                request.getCapacity()
        );

        return eventRepository.save(event);
    }
}