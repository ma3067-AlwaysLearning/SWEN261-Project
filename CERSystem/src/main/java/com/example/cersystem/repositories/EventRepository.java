package com.example.cersystem.repositories;

import com.example.cersystem.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByTitleContainingIgnoreCase(String title);
    List<Event> findByCategory(String category);

    @Query("""
        SELECT e
        FROM Event e
        WHERE (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:category IS NULL OR LOWER(e.category) = LOWER(:category))
          AND (:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%')))
          AND (:scheduledDate IS NULL OR e.scheduledDate = :scheduledDate)
        ORDER BY e.scheduledDate ASC
    """)
    List<Event> searchAndFilter(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("location") String location,
            @Param("scheduledDate") LocalDate scheduledDate
    );
}
