package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.enums.EventState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    private EventMapper() {
    }

    public static Event mapToEvent(NewEventDto newEventDto, Category category, User user) {
        Event event = null;
        if (newEventDto != null) {
            Boolean isPaid;
            if (newEventDto.getPaid() == null) {
                isPaid = false;
            } else {
                isPaid = true;
            }

            Integer participantLimit;
            if (newEventDto.getParticipantLimit() == null) {
                participantLimit = 0;
            } else {
                participantLimit = newEventDto.getParticipantLimit();
            }

            Boolean requestModeration;
            if (newEventDto.getRequestModeration() == null) {
                requestModeration = true;
            } else {
                requestModeration = false;
            }

            event = new Event(
                    0,
                    newEventDto.getAnnotation(),
                    category,
                    LocalDateTime.now(),
                    newEventDto.getDescription(),
                    newEventDto.getEventDate(),
                    user,
                    newEventDto.getLocation(),
                    isPaid,
                    participantLimit,
                    null,
                    requestModeration,
                    EventState.PENDING,
                    newEventDto.getTitle(),
                    null
            );
        }
        return event;
    }

    public static EventFullDto mapToEventFullDto(Event event) {
        EventFullDto eventFullDto = null;
        if (event != null) {
            eventFullDto = new EventFullDto(
                    event.getAnnotation(),
                    CategoryMapper.mapToCategoryDto(event.getCategory()),
                    0,
                    event.getCreatedOn(),
                    event.getDescription(),
                    event.getEventDate(),
                    event.getId(),
                    UserMapper.mapToUserShortDto(event.getInitiator()),
                    event.getLocation(),
                    event.getPaid(),
                    event.getParticipantLimit(),
                    null,
                    event.getRequestModeration(),
                    event.getState(),
                    event.getTitle(),
                    0
            );
        }
        return eventFullDto;
    }

    public static EventShortDto mapToEventShortDto(Event event) {
        EventShortDto eventShortDto = null;
        if (event != null) {
            eventShortDto = new EventShortDto(
                    event.getAnnotation(),
                    CategoryMapper.mapToCategoryDto(event.getCategory()),
                    0,
                    event.getEventDate(),
                    event.getId(),
                    UserMapper.mapToUserShortDto(event.getInitiator()),
                    event.getPaid(),
                    event.getTitle(),
                    0
            );
        }
        return eventShortDto;
    }

    public static List<EventShortDto> mapToEventShortDto(List<Event> events) {
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        if (events != null) {
            for (Event event : events) {
                eventShortDtos.add(mapToEventShortDto(event));
            }
        }
        return eventShortDtos;
    }

    public static List<EventFullDto> mapToEventFullDto(List<Event> events) {
        List<EventFullDto> eventFullDtos = new ArrayList<>();
        if (events != null) {
            for (Event event : events) {
                eventFullDtos.add(mapToEventFullDto(event));
            }
        }
        return eventFullDtos;
    }
}