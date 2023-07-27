package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ParticipationRequestMapper {

    private ParticipationRequestMapper() {
    }

    public static ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest participationRequest) {
        ParticipationRequestDto participationRequestDto = null;
        if (participationRequest != null) {
            participationRequestDto = new ParticipationRequestDto(
                    participationRequest.getId(),
                    participationRequest.getCreated(),
                    participationRequest.getEvent().getId(),
                    participationRequest.getRequester().getId(),
                    participationRequest.getStatus());
        }
        return participationRequestDto;
    }

    public static List<ParticipationRequestDto> mapToParticipationRequestDto(List<ParticipationRequest> participationRequests) {
        List<ParticipationRequestDto> participationRequestDtos = null;
        if (participationRequests != null) {
            for (ParticipationRequest participationRequest : participationRequests) {
                participationRequestDtos.add(mapToParticipationRequestDto(participationRequest));
            }
        }
        return participationRequestDtos;
    }


}