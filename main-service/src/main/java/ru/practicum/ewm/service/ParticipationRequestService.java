package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto postParticipationRequest(long userId, long eventId);

    List<ParticipationRequestDto> getParticipationRequestByUserInAllEvents(long userId);

    ParticipationRequestDto patchParticipationRequest(long userId, long requestId);

    List<ParticipationRequestDto> getParticipationRequestByUserInParticularEvent(long userId, long eventId);

    EventRequestStatusUpdateResult patchParticipationRequestsStatuses(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
