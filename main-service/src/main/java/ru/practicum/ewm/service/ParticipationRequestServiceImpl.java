package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto postParticipationRequest(long userId, long eventId) {
        if (eventId == 0) {
            eventId = 1;
        }
        Event event = eventRepository.findById(eventId).get();
        if (event.getInitiator().getId() == userId) {
            throw new RequesterMatchesInitiatorException(
                    "Инициатор события не может участвовать в собственном событии");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new UnacceptableEventStatusForParticipationException(
                    "Нельзя принять участие в еще не опубликованном событии");
        }

        if (!participationRequestRepository.getParticipationRequestByRequester_IdAndEvent_Id(userId, eventId)
                .isEmpty()) {
            throw new DuplicateParticipationRequestException(
                    "Нельзя отправлять повторный запрос от того же пользователя на то же событие");
        }

        if (event.getParticipantLimit() > 0) {
            int currentParticipationRequestsNumber =
                    participationRequestRepository.countParticipationRequestByEvent_Id(eventId);
            if (currentParticipationRequestsNumber >= event.getParticipantLimit()) {
                throw new ParticipantLimitAchievedException("Уже достигнут лимит одобренных заявок на мероприятие");
            }
        }

        User requester = userRepository.findById(userId).get();

        ParticipationRequest participationRequest = new ParticipationRequest(
                0,
                LocalDateTime.now(),
                event,
                requester,
                RequestStatus.PENDING);

        if (event.getParticipantLimit() == 0) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
        }

        ParticipationRequest savedParticipantRequest = participationRequestRepository.save(participationRequest);
        return ParticipationRequestMapper.mapToParticipationRequestDto(savedParticipantRequest);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getParticipationRequestByUserInAllEvents(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }

        List<ParticipationRequest> participationRequests =
                participationRequestRepository.getParticipationRequestByRequester_Id(userId);
        List<ParticipationRequestDto> participationRequestDtos =
                ParticipationRequestMapper.mapToParticipationRequestDto(participationRequests);

        return participationRequestDtos;
    }

    @Override
    @Transactional
    public ParticipationRequestDto patchParticipationRequest(long userId, long requestId) {
        ParticipationRequest participationRequest =
                participationRequestRepository.getParticipationRequestByRequester_IdAndId(userId, requestId);
        if (participationRequest == null) {
            throw new ParticipationRequestNotFoundException(
                    "Запрос на участие с id = " + requestId + " от пользователя с id = " + userId + " не найден");
        }

        participationRequest.setStatus(RequestStatus.CANCELED);
        ParticipationRequest patchedParticipationRequest = participationRequestRepository.save(participationRequest);
        return ParticipationRequestMapper.mapToParticipationRequestDto(patchedParticipationRequest);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getParticipationRequestByUserInParticularEvent(long userId, long eventId) {
        List<ParticipationRequest> participationRequests =
                participationRequestRepository.getParticipationRequestByEvent_Initiator_IdAndEvent_Id(userId, eventId);
        List<ParticipationRequestDto> participationRequestDtos =
                ParticipationRequestMapper.mapToParticipationRequestDto(participationRequests);

        return participationRequestDtos;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchParticipationRequestsStatuses(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        if (eventRequestStatusUpdateRequest == null) {
            throw new ParticipantLimitAchievedException("Уже достигнут лимит одобренных заявок на мероприятие");
        }

        Event event = eventRepository.findById(eventId).get();
        List<ParticipationRequest> participationRequests = new ArrayList<>();
        if (userId == event.getInitiator().getId()) {
            participationRequests = participationRequestRepository.getParticipationRequestByEvent_Id(eventId);
        }

        for (ParticipationRequest participationRequest : participationRequests) {
            if (participationRequest.getStatus() != RequestStatus.PENDING) {
                throw new ViolationParticipationRequestStatusException(
                        "Можно изменять статус только у заявок, находящихся в состоянии ожидания");
            }
        }

        List<ParticipationRequestDto> confirmedParticipationRequestDtos = new ArrayList<>();
        List<ParticipationRequestDto> rejectedParticipationRequestDtos = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getStatus() == RequestStatus.REJECTED) {
            for (ParticipationRequest participationRequest : participationRequests) {
                participationRequest.setStatus(RequestStatus.REJECTED);
                ParticipationRequestDto rejectedParticipationRequestDto =
                        ParticipationRequestMapper.mapToParticipationRequestDto(participationRequest);
                rejectedParticipationRequestDtos.add(rejectedParticipationRequestDto);
            }
        } else if (event.getRequestModeration() == false
                || eventRequestStatusUpdateRequest.getStatus() == RequestStatus.CONFIRMED) {
            if (event.getParticipantLimit() > 0) {
                int currentNumberOfParticipants =
                        participationRequestRepository.countParticipationRequestByEvent_IdAndStatus(
                                eventId, RequestStatus.CONFIRMED);
                int participantLimitForEvent = event.getParticipantLimit();

                if (currentNumberOfParticipants >= participantLimitForEvent) {
                    throw new ParticipantLimitAchievedException("Уже достигнут одобренных заявок на мероприятие");
                }

                for (ParticipationRequest participationRequest : participationRequests) {
                    if (currentNumberOfParticipants > participantLimitForEvent) {
                        participationRequest.setStatus(RequestStatus.REJECTED);
                        ParticipationRequestDto rejectedParticipationRequestDto =
                                ParticipationRequestMapper.mapToParticipationRequestDto(participationRequest);
                        rejectedParticipationRequestDtos.add(rejectedParticipationRequestDto);
                    } else {
                        participationRequest.setStatus(RequestStatus.CONFIRMED);
                        ParticipationRequestDto confirmedParticipationRequestDto =
                                ParticipationRequestMapper.mapToParticipationRequestDto(participationRequest);
                        confirmedParticipationRequestDtos.add(confirmedParticipationRequestDto);
                        currentNumberOfParticipants++;
                    }
                }
            }

            for (ParticipationRequest participationRequest : participationRequests) {
                participationRequest.setStatus(RequestStatus.CONFIRMED);
                ParticipationRequestDto confirmedParticipationRequestDto =
                        ParticipationRequestMapper.mapToParticipationRequestDto(participationRequest);
                confirmedParticipationRequestDtos.add(confirmedParticipationRequestDto);
            }

            for (ParticipationRequest participationRequest : participationRequests) {
                participationRequestRepository.save(participationRequest);
            }
        }

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult(
                confirmedParticipationRequestDtos,
                rejectedParticipationRequestDtos);
        return eventRequestStatusUpdateResult;
    }
}
