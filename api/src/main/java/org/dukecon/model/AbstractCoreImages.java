package org.dukecon.model;

import lombok.Data;

import java.util.Map;

/**
 * Created by ascheman on 27.05.17.
 */
@Data
public class AbstractCoreImages {

    private byte[] conferenceImage;
    private byte[] conferenceFavIcon;
    private Map<String, byte[]> locationImages;
    private Map<String, byte[]> locationMapImages;
    private Map<String, byte[]> languageImages;
    private Map<String, byte[]> streamImages;

}
