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
@EqualsAndHashCode(of = "order")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Track implements Identifyable {
    private String id;
    private Integer order;
    private Map<String, String> names = new HashMap<>();
    private String icon;
}
