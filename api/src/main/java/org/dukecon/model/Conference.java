package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.dukecon.model.annotations.Relation;

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
    private String homeUrl;
    private String icon;

    @Relation(relationType = Relation.RelationType.ONE_TO_ONE)
    private MetaData metaData;
    @Relation(relationType = Relation.RelationType.ONE_TO_MANY, remoteType = Event.class)
    private List<Event> events;
    @Relation(relationType = Relation.RelationType.ONE_TO_MANY, remoteType = Speaker.class)
    private List<Speaker> speakers;
}
