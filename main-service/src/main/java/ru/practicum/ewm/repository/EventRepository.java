package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> getAllEventsByInitiator_Id(long initiatorId, Pageable page);

    Event getEventByInitiator_IdAndId(long initiatorId, long id);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.initiator.id IN ?1 " +
            "AND e.state IN ?2 " +
            "AND e.category.id IN ?3 " +
            "AND e.eventDate > ?4 " +
            "AND e.eventDate < ?5")
    Page<Event> searchEvents(long[] users, String[] states, long[] categories, String rangeStart, String rangeEnd, Pageable page);

}
