package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Data
@EqualsAndHashCode(of = "code")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Track {
    private Integer order;
    private Integer code;
    private Map<Language, String> names = new HashMap<>();
}
