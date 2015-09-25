package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Talk {

    private String id;
    @Deprecated
    private String track;
    private Integer trackNumber;
    @Deprecated
    private String level;
    private Integer levelNumber;
    @Deprecated
    private String type;
    private Integer typeNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private String start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss")
    private String end;
    @Deprecated
    private String location;
    private Integer roomNumber;
    private String title;
    private List<Speaker> speakers = new ArrayList<>();
    private String abstractText;
    private String language;
    private boolean demo;

}
