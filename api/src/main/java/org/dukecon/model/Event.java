package org.dukecon.model;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.dukecon.model.annotations.Relation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Data
@ToString(of = {"id", "title"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event implements Identifyable {
    private String id;
    @JsonProperty(value = "trackId")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Relation(relationType = Relation.RelationType.MANY_TO_ONE)
    private Track track;
    @JsonProperty(value = "audienceId")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Relation(relationType = Relation.RelationType.MANY_TO_ONE)
    private Audience audience;
    @JsonProperty(value = "typeId")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Relation(relationType = Relation.RelationType.MANY_TO_ONE)
    private EventType type;
    @JsonProperty(value = "locationId")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Relation(relationType = Relation.RelationType.MANY_TO_ONE)
    private Location location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime end;
    private String title;
    @JsonProperty(value = "speakerIds")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Relation(relationType = Relation.RelationType.MANY_TO_MANY, remoteType = Speaker.class)
    private List<Speaker> speakers = new ArrayList<>();
    private String abstractText;
    @JsonProperty(value = "languageId")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Relation(relationType = Relation.RelationType.MANY_TO_ONE)
    private Language language;
    private boolean demo;
    private boolean simultan;
    /** flag if event will have many attendees */
    private boolean veryPopular;
    /** flag if event is fully booked */
    private boolean fullyBooked;
    /** number of people favoured this event */
    private Integer numberOfFavorites;
}
