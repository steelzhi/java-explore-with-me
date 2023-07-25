package ru.practicum.ewm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.state.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "main_events")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String annotation;

    @JoinColumn(name = "category_id")
    @ManyToOne
    private Category category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @JoinColumn(name = "initiator_id")
    @ManyToOne
    private User initiator;

    @JoinColumn(name = "location_id")
    @ManyToOne
    private Location location;
    private boolean paid;
    private int participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private boolean requestModeration;

    @Enumerated
    private EventState state;
    private String title;
}
