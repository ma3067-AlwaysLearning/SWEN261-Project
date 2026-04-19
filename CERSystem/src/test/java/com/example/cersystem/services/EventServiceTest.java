package com.example.cersystem.services;

import com.example.cersystem.dto.EventRequest;
import com.example.cersystem.dto.EventSummaryResponse;
import com.example.cersystem.models.Event;
import com.example.cersystem.models.Role;
import com.example.cersystem.models.User;
import com.example.cersystem.repositories.EventRepository;
import com.example.cersystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private User organizer;
    private User student;
    private Event event;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(1L);
        organizer.setName("Organizer");
        organizer.setEmail("organizer@rit.edu");
        organizer.setPassword("123");
        organizer.setRole(Role.ORGANIZER);
        organizer.setUniversity_id("ORG001");

        student = new User();
        student.setId(2L);
        student.setName("Student");
        student.setEmail("student@rit.edu");
        student.setPassword("123");
        student.setRole(Role.STUDENT);
        student.setUniversity_id("764000269");
        student.setEvents(new ArrayList<>());

        event = new Event(
                organizer,
                LocalDateTime.of(2026, 5, 10, 12, 0),
                LocalDateTime.of(2026, 5, 10, 10, 0),
                LocalDateTime.of(2026, 5, 9, 23, 59),
                LocalDateTime.of(2026, 5, 1, 8, 0),
                "Building A",
                "Workshop",
                "Java workshop",
                LocalDate.of(2026, 5, 10),
                "Java Basics",
                2
        );
        event.setEventId(1L);
        event.setStatus("SCHEDULED");
    }

    @Test
    void testSaveEvent() {
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.save(event);

        assertNotNull(result);
        assertEquals("Java Basics", result.getTitle());
        verify(eventRepository).save(event);
    }

    @Test
    void testGetAllEvents() {
        when(eventRepository.findAll()).thenReturn(List.of(event));

        List<Event> result = eventService.getAll();

        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        verify(eventRepository).findAll();
    }

    @Test
    void testSearchByName() {
        when(eventRepository.findByTitleContainingIgnoreCase("java"))
                .thenReturn(List.of(event));

        List<Event> result = eventService.searchByName("java");

        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        verify(eventRepository).findByTitleContainingIgnoreCase("java");
    }

    @Test
    void testSearchByCategory() {
        when(eventRepository.findByCategoryIgnoreCase("Workshop"))
                .thenReturn(List.of(event));

        List<Event> result = eventService.searchByCategory("Workshop");

        assertEquals(1, result.size());
        assertEquals("Workshop", result.get(0).getCategory());
        verify(eventRepository).findByCategoryIgnoreCase("Workshop");
    }

    @Test
    void testSearchAndFilter() {
        when(eventRepository.findAll(any(Specification.class))).thenReturn(List.of(event));

        List<Event> result = eventService.searchAndFilter(
                "java",
                "Workshop",
                "Building",
                "organizer",
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 31)
        );

        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    void testGetRegisteredEvents() {
        student.setEvents(new ArrayList<>(List.of(event)));
        when(userRepository.findByEmail("student@rit.edu")).thenReturn(Optional.of(student));

        List<Event> result = eventService.getRegisteredEvents("student@rit.edu");

        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        verify(userRepository).findByEmail("student@rit.edu");
    }

    @Test
    void testRegisterForEvent_UserNotFound() {
        when(userRepository.findByEmail("student@rit.edu")).thenReturn(Optional.empty());

        Map<String, Object> result = eventService.registerForEvent(1L, "student@rit.edu");

        assertEquals(false, result.get("success"));
        assertEquals("You must be logged in", result.get("message"));
        verify(userRepository).findByEmail("student@rit.edu");
    }

    @Test
    void testRegisterForEvent_EventNotFound() {
        when(userRepository.findByEmail("student@rit.edu")).thenReturn(Optional.of(student));
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        Map<String, Object> result = eventService.registerForEvent(1L, "student@rit.edu");

        assertEquals(false, result.get("success"));
        assertEquals("Event does not exist", result.get("message"));
        verify(eventRepository).findById(1L);
    }

    @Test
    void testRegisterForEvent_EventFull() {
        when(userRepository.findByEmail("student@rit.edu")).thenReturn(Optional.of(student));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.countByEventsEventId(1L)).thenReturn(2);

        Map<String, Object> result = eventService.registerForEvent(1L, "student@rit.edu");

        assertEquals(false, result.get("success"));
        assertEquals("This event is full", result.get("message"));
    }

    @Test
    void testRegisterForEvent_AlreadyRegistered() {
        student.setEvents(new ArrayList<>(List.of(event)));

        when(userRepository.findByEmail("student@rit.edu")).thenReturn(Optional.of(student));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.countByEventsEventId(1L)).thenReturn(1);

        Map<String, Object> result = eventService.registerForEvent(1L, "student@rit.edu");

        assertEquals(false, result.get("success"));
        assertEquals("You are already registered for this event", result.get("message"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterForEvent_Success() {
        when(userRepository.findByEmail("student@rit.edu")).thenReturn(Optional.of(student));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(userRepository.countByEventsEventId(1L)).thenReturn(0);

        Map<String, Object> result = eventService.registerForEvent(1L, "student@rit.edu");

        assertEquals(true, result.get("success"));
        assertEquals(1, student.getEvents().size());
        verify(userRepository).save(student);
    }

    @Test
    void testCreateEvent() {
        EventRequest request = new EventRequest();
        request.setTitle("Cyber Seminar");
        request.setDescription("Security basics");
        request.setScheduledDate(LocalDate.of(2026, 6, 1));
        request.setCategory("Seminar");
        request.setLocation("Auditorium");
        request.setCapacity(50);
        request.setRegistrationStart(LocalDateTime.of(2026, 5, 20, 9, 0));
        request.setRegistrationEnd(LocalDateTime.of(2026, 5, 31, 23, 59));
        request.setStartTime(LocalDateTime.of(2026, 6, 1, 10, 0));
        request.setEndTime(LocalDateTime.of(2026, 6, 1, 12, 0));

        Event savedEvent = new Event(
                organizer,
                LocalDateTime.of(2026, 6, 1, 12, 0),
                LocalDateTime.of(2026, 6, 1, 10, 0),
                LocalDateTime.of(2026, 5, 31, 23, 59),
                LocalDateTime.of(2026, 5, 20, 9, 0),
                "Auditorium",
                "Seminar",
                "Security basics",
                LocalDate.of(2026, 6, 1),
                "Cyber Seminar",
                50
        );

        when(userRepository.findByEmail("organizer@rit.edu")).thenReturn(Optional.of(organizer));
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        Event result = eventService.createEvent(request, "organizer@rit.edu");

        assertNotNull(result);
        assertEquals("Cyber Seminar", result.getTitle());
        assertEquals("Seminar", result.getCategory());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void testCreateEvent_OrganizerNotFound() {
        EventRequest request = new EventRequest();
        when(userRepository.findByEmail("organizer@rit.edu")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                eventService.createEvent(request, "organizer@rit.edu"));

        assertEquals("Organizer not found", exception.getMessage());
    }

    @Test
    void testGetAllSummaries() {
        when(eventRepository.findAll(any(Specification.class))).thenReturn(List.of(event));
        when(userRepository.countByEventsEventId(1L)).thenReturn(1);

        List<EventSummaryResponse> result = eventService.getAllSummaries(
                null, null, null, null, null, null
        );

        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).title());
        assertEquals(1, result.get(0).spotLeft());
        verify(userRepository).countByEventsEventId(1L);
    }
}