package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.status.EventState;
import ru.practicum.ewm.status.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class EventRequestStatusUpdateRequest {

    private List<Integer> requestIds;
    private RequestStatus status;
}
