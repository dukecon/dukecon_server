package org.dukecon.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dukecon.model.annotations.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Speaker implements Identifyable {
    @Relation(relationType = Relation.RelationType.MANY_TO_ONE)
    private Conference conference;
    private String id;
    private String name;
    private String company;
    private String email;
    private String website;
    private String twitter;
    private String gplus;
    private String bio;

    /**
     * @deprecated will be removed in v2 as we only need a speaker order per talk
     */
    @Deprecated
    private boolean defaultSpeaker;

    @JsonProperty(value = "eventIds")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Event> events = new ArrayList<>();
}
