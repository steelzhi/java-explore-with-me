package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.status.EventState;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> getAllEventsByInitiator_Id(long initiatorId, Pageable page);

    Event getEventByInitiator_IdAndId(long initiatorId, long id);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.initiator.id IN ?1 " +
            "AND e.state IN ?2 " +
            "AND e.category.id IN ?3 " +
            "AND e.eventDate >= COALESCE(?4, e.eventDate) " +
            "AND e.eventDate <= COALESCE(?5, e.eventDate)")
    Page<Event> searchEvents(Long[] users, EnumSet<EventState> enumSetOfStates, Long[] categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.state IN ?1")
    List<Event> searchEvents2(EnumSet<EventState> states);

}
