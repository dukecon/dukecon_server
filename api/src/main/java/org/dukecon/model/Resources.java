package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Created by christoferdutz on 16.10.16.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resources extends AbstractCoreImages {
    private Styles styles;
    private Map<String, byte[]> speakerImages;
}
