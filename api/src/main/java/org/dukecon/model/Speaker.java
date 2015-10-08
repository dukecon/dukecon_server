package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Speaker {
    private String id;
    private String name;
    private String company;
    private String email;
    private String website;
    private String twitter;
    private String gplus;
    private String bio;
    private boolean defaultSpeaker;

}
