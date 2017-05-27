package org.dukecon.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Created by ascheman on 27.05.17.
 */
@Data
public class AbstractCoreImages {

    private byte[] conferenceImage;
    private Map<String, byte[]> locationImages;
    private Map<String, byte[]> locationMapImages;
    private Map<String, byte[]> languageImages;
    private Map<String, byte[]> streamImages;

}
