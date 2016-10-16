package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Created by christoferdutz on 16.10.16.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resources {

    private Styles styles;
    private byte[] conferenceImage;
    private Map<String, byte[]> locationImages;
    private Map<String, byte[]> locationMapImages;
    private Map<String, byte[]> languageImages;
    private Map<String, byte[]> streamImages;
    private Map<String, byte[]> speakerImages;

}
