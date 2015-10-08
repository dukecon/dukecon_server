package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Conference {
    private String id;
    private String name;
    private String url;
    private MetaData metaData;
    private List<Track> tracks;
    private List<Room> rooms;
    private List<Talk> talks;
    private List<Speaker> speakers;
}
