package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.EventNotFoundException;
import ru.practicum.ewm.exception.IncorrectEventRequestException;
import ru.practicum.ewm.exception.PatchRestrictionException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.state.EventState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    @Override
    public EventFullDto postEvent(long userId, NewEventDto newEventDto) {
        int catId = newEventDto.getCategory();
        Category category = getCategoryById(catId);

        User user = getUserById(userId);

        Event event = EventMapper.mapToEvent(newEventDto, category, user);
        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(savedEvent);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEventsAddedByUser(long userId, Integer from, Integer size) {
        List<Event> events = new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
        Page<Event> pagedList = eventRepository.getAllEventsByInitiator_Id(userId, page);


        if (pagedList != null) {
            events = pagedList.getContent();
        }

        return EventMapper.mapToEventShortDto(events);
    }

    @Override
    public EventFullDto getEventAddedByUser(long userId, long eventId) {
        Event event = eventRepository.getEventByInitiator_IdAndId(userId, eventId);
        checkIfEventExists(event);
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public EventFullDto patchEventByUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.getEventByInitiator_IdAndId(userId, eventId);
        checkIfEventExists(event);
        checkIfEventStateRestrictsToPatch(event.getState());

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category newCategory = getCategoryById(updateEventUserRequest.getCategory());
            event.setCategory(newCategory);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(updateEventUserRequest.getLocation());
        }
        event.setPaid(updateEventUserRequest.isPaid());
        if (updateEventUserRequest.getParticipantLimit() != event.getParticipantLimit()) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        event.setRequestModeration(updateEventUserRequest.isRequestModeration());
        if (updateEventUserRequest.getStateAction() != null) {
            event.setState(updateEventUserRequest.getStateAction());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        eventRepository.save(event);
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public List<EventFullDto> searchEvents(
            long[] users,
            String[] states,
            long[] categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size) {
        List<Event> events = new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());

        Page<Event> pagedList = eventRepository.searchEvents(users, states, categories, rangeStart, rangeEnd, page);

        if (pagedList != null) {
            events = pagedList.getContent();
        }

        return EventMapper.mapToEventFullDto(events);
    }

    @Override
    public EventFullDto patchEventByAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId).get();
        checkIfEventExists(event);
        checkIfEventStateRestrictsToPatch(event.getState());
        if (updateEventAdminRequest.getEventDate() != null) {
            checkIfEventTimeRestrictsToPatch(updateEventAdminRequest.getEventDate());
        } else {
            checkIfEventTimeRestrictsToPatch(event.getEventDate());
        }

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category newCategory = getCategoryById(updateEventAdminRequest.getCategory());
            event.setCategory(newCategory);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(updateEventAdminRequest.getLocation());
        }
        event.setPaid(updateEventAdminRequest.isPaid());
        if (updateEventAdminRequest.getParticipantLimit() != event.getParticipantLimit()) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        event.setRequestModeration(updateEventAdminRequest.isRequestModeration());
        if (updateEventAdminRequest.getStateAction() != null) {
            event.setState(updateEventAdminRequest.getStateAction());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        eventRepository.save(event);
        return EventMapper.mapToEventFullDto(event);
    }

    private Category getCategoryById(long catId) {
        return categoryRepository.findById(catId).get();
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).get();
    }

    private void checkIfEventExists(Event event) {
        if (event == null) {
            throw new EventNotFoundException("Событие не найдено или недоступно");
        }
    }

    private void checkIfEventStateRestrictsToPatch(EventState eventState) {
        if (eventState == EventState.PUBLISHED) {
            throw new PatchRestrictionException("Нельзя вносить изменения в уже опубликованные события");
        }
    }

    private void checkIfEventTimeRestrictsToPatch(LocalDateTime eventDateTime) {
        if (eventDateTime.minusHours(1).isBefore(LocalDateTime.now())) {
            throw new IncorrectEventRequestException("Обновленное время начала события не можем быть меньше часа от времени публикации");
        }
    }
}
