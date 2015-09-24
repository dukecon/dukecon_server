package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaData {
    private Conference conference;
    private List<Room> rooms = new ArrayList<>();
    private List<Language> languages = new ArrayList<>();
    private Language defaultLanguage;
    private List<Track> tracks = new ArrayList<>();
    private List<Audience> audiences = new ArrayList<>();
}
