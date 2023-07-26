package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.state.EventState;
import ru.practicum.ewm.util.ControllerParamChecker;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@PathVariable long userId, @RequestBody NewEventDto newEventDto) {
        ControllerParamChecker.checkIfEventParamsAreNotCorrect(newEventDto);
        return eventService.postEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsAddedByUser(@PathVariable long userId,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsAddedByUser(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventAddedByUser(@PathVariable long userId,
                                            @PathVariable long eventId) {
        return eventService.getEventAddedByUser(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchEventByUser(@PathVariable long userId,
                                   @PathVariable long eventId,
                                   @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        ControllerParamChecker.checkIfEventParamsAreNotCorrect(updateEventUserRequest);
        return eventService.patchEventByUser(userId, eventId, updateEventUserRequest);
    }


// нужно добавить еще 2 эндпоинта
/*    @GetMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<> getParticipationRequestsFromUser() {




    }


    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    */


    @GetMapping("/admin/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEvents(@RequestParam(required = false) Long[] users,
                                           @RequestParam(required = false) EnumSet<EventState> states,
                                           @RequestParam(required = false) Long[] categories,
                                           @RequestParam(required = false) String rangeStart,
                                           @RequestParam(required = false) String rangeEnd,
                                           @RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchEventByAdmin(@PathVariable long eventId,
                                   @RequestBody UpdateEventAdminRequest updateEventUserRequest) {
        ControllerParamChecker.checkIfEventParamsAreNotCorrect(updateEventUserRequest);
        return eventService.patchEventByAdmin(eventId, updateEventUserRequest);
    }
}
