package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.status.EventState;

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
