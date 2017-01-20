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
    private String id;
    private String name;
    private String firstname;
    private String lastname;
    private String company;
    private String email;
    private String website;
    private String twitter;
    private String gplus;
    private String facebook;
    private String xing;
    private String linkedin;
    private String bio;
    private String photoId;

    @JsonProperty(value = "eventIds")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Relation(relationType = Relation.RelationType.MANY_TO_MANY, remoteType = Event.class)
    private List<Event> events = new ArrayList<>();
}
