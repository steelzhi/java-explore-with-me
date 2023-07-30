package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventClient eventClient;


    @Override
    @Transactional
    public EventFullDto postEvent(long userId, NewEventDto newEventDto) {
        checkIfEventParamsAreNotCorrect(newEventDto);

        int catId = newEventDto.getCategory();
        Category category = getCategoryById(catId);

        User user = getUserById(userId);

        Event event = EventMapper.mapToEvent(newEventDto, category, user);
        Event savedEvent = eventRepository.save(event);
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(savedEvent);

        return eventFullDto;
    }

    @Override
    @Transactional
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
    @Transactional
    public EventFullDto getEventAddedByUser(long userId, long eventId) {
        Event event = eventRepository.getEventByInitiator_IdAndId(userId, eventId);
        checkIfEventExists(event);
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto patchEventByUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        checkIfEventParamsAreNotCorrect(updateEventUserRequest);

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
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != event.getParticipantLimit()
                && updateEventUserRequest.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() == EventState.CANCEL_REVIEW) {
            event.setState(EventState.CANCELED);
        } else {
            event.setState(EventState.PENDING);
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        Event savedEvent = eventRepository.save(event);
        return EventMapper.mapToEventFullDto(savedEvent);
    }

    @Override
    @Transactional
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
        Page<Event> pagedList =
                eventRepository.searchInitiatorEvents(users, states, categories, startTime, endTime, page);

        if (pagedList != null) {
            events = pagedList.getContent();
        }

        List<EventFullDto> eventFullDtos = new ArrayList<>();
        for (Event event : events) {
            int numberOfParticipationRequests =
                    participationRequestRepository.countParticipationRequestByEvent_Id(event.getId());
            EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);
            eventFullDto.setConfirmedRequests(numberOfParticipationRequests);
            eventFullDtos.add(eventFullDto);
        }

        return eventFullDtos;
    }

    @Override
    @Transactional
    public EventFullDto patchEventByAdmin(long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        checkIfEventParamsAreNotCorrect(updateEventAdminRequest);

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
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != event.getParticipantLimit()
                && updateEventAdminRequest.getParticipantLimit() != 0) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    checkIfEventWasCanceled(eventId);
                    event.setState(EventState.PUBLISHED);
                    break;
                default:
                    event.setState(EventState.PENDING);
            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        Event savedEvent = eventRepository.save(event);
        return EventMapper.mapToEventFullDto(savedEvent);
    }

    @Override
    @Transactional
    public List<EventShortDto> getPublishedEvents(
            String text,
            Long[] categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            boolean onlyAvailable,
            EventSort sort,
            Integer from,
            Integer size,
            HttpServletRequest request) {

        isTextANumber(text);

        if (categories == null) {
            List<Category> allCategories = categoryRepository.findAll();
            List<Long> allCategoriesIds = allCategories.stream()
                    .map(category -> category.getId())
                    .collect(Collectors.toList());

            categories = new Long[allCategories.size()];
            for (int i = 0; i < allCategories.size(); i++) {
                categories[i] = allCategoriesIds.get(i);
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startTime;
        if (rangeStart != null) {
            startTime = LocalDateTime.parse(rangeStart, formatter);
        } else {
            startTime = now;
        }
        LocalDateTime endTime = null;
        if (rangeEnd != null) {
            endTime = LocalDateTime.parse(rangeEnd, formatter);
        }

        List<Event> events = new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("id").descending());
        Page<Event> pagedList = eventRepository.searchPublishedEvents(
                categories,
                paid,
                startTime,
                endTime,
                EventState.PUBLISHED,
                text,
                page);

        if (pagedList != null) {
            events = pagedList.getContent();
        }

        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            int currentNumberOfParticipants =
                    participationRequestRepository.countParticipationRequestByEvent_IdAndStatus(
                            event.getId(),
                            RequestStatus.CONFIRMED);

            if (onlyAvailable && event.getParticipantLimit() <= currentNumberOfParticipants) {
                break;
            }

            EventShortDto eventShortDto = EventMapper.mapToEventShortDto(event);
            eventShortDto.setConfirmedRequests(currentNumberOfParticipants);
            eventShortDtos.add(eventShortDto);
        }

        if (sort == EventSort.EVENT_DATE) {
            eventShortDtos.sort((event1, event2) -> {
                if (event1.getEventDate().isBefore(event2.getEventDate())
                        || event1.getEventDate().equals(event2.getEventDate())) {
                    return -1;
                } else {
                    return 1;
                }
            });
        } else {
            eventShortDtos.sort((event1, event2) -> {
                if (event1.getViews() <= event2.getViews()) {
                    return -1;
                } else {
                    return 1;
                }
            });
        }

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        Hit hit = new Hit(0, "ewm-main-service", uri, ip, now);
        eventClient.postHit(hit);

        if (!eventShortDtos.isEmpty()) {
            List<String> urisList = new ArrayList<>();
            for (EventShortDto eventShortDto : eventShortDtos) {
                urisList.add("/events/" + eventShortDto.getId());
            }

            String[] urisArray = new String[urisList.size()];
            urisList.toArray(urisArray);

            String encodedDateStart = null;
            String encodedDateEnd = null;

            try {
                if (rangeStart != null) {
                    encodedDateStart = URLEncoder.encode(rangeStart, "utf-8");
                }
                if (rangeEnd != null) {
                    encodedDateEnd = URLEncoder.encode(rangeEnd, "utf-8");
                }
            } catch (UnsupportedEncodingException e) {
                throw new CodingException("Ошибка кодирования дат");
            }

            ResponseEntity<Object> statsEntity = eventClient.getStats(
                    encodedDateStart,
                    encodedDateEnd,
                    urisArray,
                    false);

            ArrayList<LinkedHashMap<String, Object>> body =
                    (ArrayList<LinkedHashMap<String, Object>>) statsEntity.getBody();
            if (!body.isEmpty()) {
                for (int i = 0; i < body.size(); i++) {
                    for (EventShortDto eventShortDto : eventShortDtos) {
                        if (body.get(i).get("uri").equals("events/" + eventShortDto.getId())) {
                            eventShortDto.setViews((int) body.get(i).get("hits"));
                        }
                    }
                }
            }
        }

        return eventShortDtos;
    }

    @Override
    @Transactional
    public EventFullDto getPublishedEvent(long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id).get();
        checkIfEventIsNotPublished(event);
        EventFullDto eventFullDto = EventMapper.mapToEventFullDto(event);

        String ip = request.getRemoteAddr();
        String uri = request.getRequestURI();
        Hit hit = new Hit(0, "ewm-main-service", uri, ip, LocalDateTime.now());
        eventClient.postHit(hit);

        if (eventFullDto != null) {
            String[] urisArray = new String[1];
            urisArray[0] = "/events/" + eventFullDto.getId();
            ResponseEntity<Object> statsEntity = eventClient.getStats(
                    null,
                    null,
                    urisArray,
                    true);

            ArrayList<LinkedHashMap<String, Object>> body =
                    (ArrayList<LinkedHashMap<String, Object>>) statsEntity.getBody();
            if (!body.isEmpty()) {
                eventFullDto.setViews((int) body.get(0).get("hits"));
            }
        }

        return eventFullDto;
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
            throw new IncorrectEventRequestException(
                    "Обновленное время начала события не можем быть меньше часа от времени публикации");
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

    private void checkIfEventParamsAreNotCorrect(NewEventDto newEventDto) {
        if (newEventDto.getEventDate().minusHours(2).isBefore(LocalDateTime.now())) {
            throw new IncorrectEventRequestException(
                    "Дата начала события не может быть раньше, чем через 2 часа от настоящего момента");
        }

        if (newEventDto.getAnnotation() == null || newEventDto.getAnnotation().isBlank()) {
            throw new IncorrectEventRequestException("Краткое описание не должно быть пустым");
        }

        if (newEventDto.getAnnotation().length() < 20 || newEventDto.getAnnotation().length() > 2000) {
            throw new IncorrectEventRequestException(
                    "Попытка добавления краткого описания со слишком маленьким или слишком большим количество символов");
        }

        if (newEventDto.getDescription() == null || newEventDto.getDescription().isBlank()) {
            throw new IncorrectEventRequestException("Полное описание не должно быть пустым");
        }

        if (newEventDto.getDescription().length() < 20 || newEventDto.getDescription().length() > 7000) {
            throw new IncorrectEventRequestException(
                    "Попытка добавления полного описания со слишком маленьким или слишком большим количество символов");
        }

        if (newEventDto.getTitle() == null || newEventDto.getTitle().isBlank()) {
            throw new IncorrectEventRequestException("Заголовок не должен быть пустым");
        }

        if (newEventDto.getTitle().length() < 3 || newEventDto.getTitle().length() > 120) {
            throw new IncorrectEventRequestException(
                    "Попытка добавления заголовка со слишком маленьким или слишком большим количество символов");
        }
    }

    private void checkIfEventParamsAreNotCorrect(UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getAnnotation() != null
                && (updateEventUserRequest.getAnnotation().length() < 20
                || updateEventUserRequest.getAnnotation().length() > 2000)) {
            throw new IncorrectEventRequestException(
                    "Попытка добавления краткого описания со слишком маленьким или слишком большим количеством " +
                            "символов");
        }

        if (updateEventUserRequest.getDescription() != null
                && (updateEventUserRequest.getDescription().length() < 20
                || updateEventUserRequest.getDescription().length() > 7000)) {
            throw new IncorrectEventRequestException(
                    "Попытка добавления полного описания со слишком маленьким или слишком большим количество символов");
        }

        if (updateEventUserRequest.getTitle() != null
                && (updateEventUserRequest.getTitle().length() < 3
                || updateEventUserRequest.getTitle().length() > 120)) {
            throw new IncorrectEventRequestException(
                    "Попытка добавления заголовка со слишком маленьким или слишком большим количество символов");
        }

        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectEventRequestException(
                    "Дата начала события не может быть раньше, чем через 2 часа от настоящего момента");
        }
    }

    private void checkIfEventParamsAreNotCorrect(UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getAnnotation() != null
                && (updateEventAdminRequest.getAnnotation().length() < 20
                || updateEventAdminRequest.getAnnotation().length() > 2000)) {
            throw new IncorrectEventRequestException(
                    "Попытка добавления краткого описания со слишком маленьким или слишком большим количество " +
                            "символов");
        }

        if (updateEventAdminRequest.getDescription() != null
                && (updateEventAdminRequest.getDescription().length() < 20
                || updateEventAdminRequest.getDescription().length() > 7000)) {
            throw new IncorrectEventRequestException(
                    "Попытка добавления полного описания со слишком маленьким или слишком большим количество символов");
        }

        if (updateEventAdminRequest.getTitle() != null
                && (updateEventAdminRequest.getTitle().length() < 3
                || updateEventAdminRequest.getTitle().length() > 120)) {
            throw new IncorrectEventRequestException(
                    "Попытка добавления заголовка со слишком маленьким или слишком большим количество символов");
        }
    }

    private void isTextANumber(String text) {
        try {
            Integer.parseInt(text);
            throw new IncorrectEventRequestException("Текст не может быть числом");
        } catch (NumberFormatException e) {
            return; // заглушка для соблюдения условий Checkstyle на Github
        }
    }

    private void checkIfEventWasCanceled(long eventId) {
        Event event = eventRepository.findById(eventId).get();
        if (event.getState() == EventState.CANCELED) {
            throw new CanceledEventException("Нельзя опубликовать отмененное событие");
        }
    }

    private void checkIfEventIsNotPublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventNotFoundException("Информация о событии не найдена");
        }
    }
}
