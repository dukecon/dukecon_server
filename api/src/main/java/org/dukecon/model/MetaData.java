package org.dukecon.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;

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
public class MetaData {
//    @JsonProperty(value = "conferenceId")
//    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//    @JsonIdentityReference(alwaysAsId = true)
//    private Conference conference;
    private List<Audience> audiences = new ArrayList<>();
    private List<TalkType> talkTypes = new ArrayList<>();
    private List<Language> languages = new ArrayList<>();
    private Language defaultLanguage;
}
