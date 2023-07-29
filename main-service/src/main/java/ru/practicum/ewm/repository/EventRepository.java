package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.enums.EventState;

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
    Page<Event> searchInitiatorEvents(Long[] users,
                                      EnumSet<EventState> enumSetOfStates,
                                      Long[] categories,
                                      LocalDateTime startTime,
                                      LocalDateTime endTime,
                                      Pageable page);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.category.id IN ?1 " +
            "AND e.paid = COALESCE(?2, e.paid) " +
            "AND e.eventDate >= COALESCE(?3, e.eventDate) " +
            "AND e.eventDate <= COALESCE(?4, e.eventDate) " +
            "AND e.state = ?5 " +
            "AND LOWER(e.annotation) LIKE COALESCE(LOWER(CONCAT('%', ?6,'%')), LOWER(e.annotation)) " +
            "OR LOWER(e.description) LIKE COALESCE(LOWER(CONCAT('%', ?6,'%')), LOWER(e.description))")
    Page<Event> searchPublishedEvents(Long[] categories,
                                      Boolean paid,
                                      LocalDateTime startTime,
                                      LocalDateTime endTime,
                                      EventState state,
                                      String text,
                                      Pageable page);

    int countAllByCategory_Id(long categoryId);

    List<Event> getAllEventsByIdInAndState(List<Long> ids, EventState state);
}
