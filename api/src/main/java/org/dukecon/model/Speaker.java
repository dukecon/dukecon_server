package org.dukecon.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String firstname;
    @NonNull
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
    private List<Event> events = new ArrayList<>();
}
