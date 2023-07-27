package ru.practicum.ewm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.status.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "participation_requests")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JoinColumn(name = "event")
    @ManyToOne
    private Event event;

    @JoinColumn(name = "requester")
    @ManyToOne
    private User requester;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}

