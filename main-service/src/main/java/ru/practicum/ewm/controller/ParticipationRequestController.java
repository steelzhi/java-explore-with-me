package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class ParticipationRequestController {
    private final ParticipationRequestService participationRequestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postParticipationRequest(@PathVariable long userId, @RequestParam long eventId) {
        return participationRequestService.postParticipationRequest(userId, eventId);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getParticipationRequestByUserInAllEvents(@PathVariable long userId) {
        return participationRequestService.getParticipationRequestByUserInAllEvents(userId);

    }

    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto patchParticipationRequest(@PathVariable long userId, @PathVariable long requestId) {
        return participationRequestService.patchParticipationRequest(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getParticipationRequestByUserInParticularEvent(
            @PathVariable long userId,
            @PathVariable long eventId) {
        return participationRequestService.getParticipationRequestByUserInParticularEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchParticipationRequestsStatuses(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody(required = false) EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return participationRequestService.patchParticipationRequestsStatuses(
                userId,
                eventId,
                eventRequestStatusUpdateRequest);
    }
}
