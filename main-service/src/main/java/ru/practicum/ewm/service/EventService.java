package ru.practicum.ewm.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.state.EventState;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

public interface EventService {
    EventFullDto postEvent(long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsAddedByUser(long userId, Integer from, Integer size);

    EventFullDto getEventAddedByUser(long userId, long eventId);

    EventFullDto patchEventByUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> searchEvents(
            Long[] users,
            EnumSet<EventState> states,
            Long[] categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);

    EventFullDto patchEventByAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
