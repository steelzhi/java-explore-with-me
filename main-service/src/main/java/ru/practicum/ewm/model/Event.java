package ru.practicum.ewm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.enums.EventState;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "main_events")
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
    @ManyToOne(cascade = {CascadeType.ALL})
    private Location location;
    private Boolean paid;
    private Integer participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private EventState state;
    private String title;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "compilation_id", referencedColumnName = "id"))
    private List<Compilation> compilations = new ArrayList<>();
}
