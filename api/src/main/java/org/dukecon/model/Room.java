package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Room implements OrderAndNamable {
    private Integer order;
    private String name;

    @Override
    public Map<Language, String> getNames() {
        Map<Language, String> temp = new HashMap<>();
        temp.put(Language.builder().code("de").build(), name);
        return temp;
    }
}
