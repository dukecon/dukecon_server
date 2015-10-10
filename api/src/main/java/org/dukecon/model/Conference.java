package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

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
public class Conference {
    private String id;
    private String name;
    private String url;
    private MetaData metaData;
    private List<Event> events;
    private List<Speaker> speakers;
}
