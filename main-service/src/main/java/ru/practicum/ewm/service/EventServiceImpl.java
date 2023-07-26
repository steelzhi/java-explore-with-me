package ru.practicum.ewm.service;

import com.sun.jdi.event.EventSet;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

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
        checkIfNewDateTimeIsNotAcceptable(updateEventUserRequest);
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
        if (updateEventUserRequest.getParticipantLimit() != event.getParticipantLimit()
                && updateEventUserRequest.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        event.setState(EventState.PENDING);
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        Event savedEvent = eventRepository.save(event);
        return EventMapper.mapToEventFullDto(savedEvent);
    }

    @Override
    public List<EventFullDto> searchEvents(
            Long[] users,
            EnumSet<EventState> states,
            Long[] categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = null;
        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        }
        LocalDateTime endTime = null;
        if (rangeStart != null) {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }

        checkIfSearchParamsAreNotCorrect(startTime, endTime, from, size);

        List<Event> events = new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
        if (users == null) {
            List<User> allUsers = userRepository.findAll();
            List<Long> allUsersIds = allUsers.stream()
                    .map(user -> user.getId())
                    .collect(Collectors.toList());
            users = new Long[allUsersIds.size()];
            allUsersIds.toArray(users);
        }
        if (states == null) {
            states = EnumSet.allOf(EventState.class);
        }
        if (categories == null) {
            List<Category> allCategories = categoryRepository.findAll();
            List<Long> allCategoriesIds = allCategories.stream()
                    .map(category -> category.getId())
                    .collect(Collectors.toList());
            categories = new Long[allCategoriesIds.size()];
            allCategoriesIds.toArray(categories);
        }
        Page<Event> pagedList = eventRepository.searchEvents(users, states, categories, startTime, endTime, page);

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
        if (updateEventAdminRequest.getParticipantLimit() != event.getParticipantLimit()
                && updateEventAdminRequest.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

/*        if (updateEventAdminRequest.getStateAction() != null) {
            event.setState(updateEventAdminRequest.getStateAction());
        } else {
            EventState currentState = event.getState();
            switch (currentState) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PUBLISHED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }*/

        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
            }
        }

        //event.setState(EventState.CANCELED);
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        Event savedEvent = eventRepository.save(event);
        return EventMapper.mapToEventFullDto(savedEvent);
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

    private void checkIfSearchParamsAreNotCorrect(LocalDateTime startTime,
                                                  LocalDateTime endTime,
                                                  Integer from,
                                                  Integer size) {
        if ((startTime != null && endTime != null && endTime.isBefore(startTime))
        || from < 0
        || size < 0) {
            throw new IncorrectEventRequestException("В поисковом запросе заданы некорректные параметры");
        }
    }

    private void checkIfNewDateTimeIsNotAcceptable(UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectEventRequestException(
                    "Дата начала события не может быть раньше, чем через 2 часа от настоящего момента");
        }
    }


}
