package org.dukecon.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import org.dukecon.model.annotations.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaData implements Identifyable {
    private String id;
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
    private String defaultIcon;
}
