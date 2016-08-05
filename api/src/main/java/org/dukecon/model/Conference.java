package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.dukecon.model.annotations.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Data
@ToString(of = {"id", "name"})
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Conference implements Identifyable {
    private String id;
    private String name;
    private String url;
    private String icon;
    private String defaultIcon;

    @Relation(relationType = Relation.RelationType.ONE_TO_MANY, remoteType = Audience.class)
    private List<Audience> audiences = new ArrayList<>();
    @Relation(relationType = Relation.RelationType.ONE_TO_MANY, remoteType = EventType.class)
    private List<EventType> eventTypes = new ArrayList<>();
    @Relation(relationType = Relation.RelationType.ONE_TO_MANY, remoteType = Language.class)
    private List<Language> languages = new ArrayList<>();
    @Relation(relationType = Relation.RelationType.ONE_TO_ONE)
    private Language defaultLanguage;
    @Relation(relationType = Relation.RelationType.ONE_TO_MANY, remoteType = Track.class)
    private List<Track> tracks = new ArrayList<>();
    @Relation(relationType = Relation.RelationType.ONE_TO_MANY, remoteType = Location.class)
    private List<Location> locations = new ArrayList<>();

    @Relation(relationType = Relation.RelationType.ONE_TO_MANY, remoteType = Event.class)
    private List<Event> events;
    @Relation(relationType = Relation.RelationType.ONE_TO_MANY, remoteType = Speaker.class)
    private List<Speaker> speakers;

    @Override
    public String getId() {
        return id;
    }
}
