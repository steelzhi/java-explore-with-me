package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> getParticipationRequestByRequester_Id(long requesterId);

    ParticipationRequest getParticipationRequestByRequester_IdAndId(long requesterId, long id);

    List<ParticipationRequest> getParticipationRequestByRequester_IdAndEvent_Id(long requesterId, long eventId);

    List<ParticipationRequest> getParticipationRequestByEvent_Initiator_IdAndEvent_Id(long eventInitiatorId, long eventId);

    int countParticipationRequestByEvent_IdAndStatus(long eventId, RequestStatus status);

    int countParticipationRequestByEvent_Id(long eventId);

    List<ParticipationRequest> getParticipationRequestByEvent_Id(long eventId);
}
