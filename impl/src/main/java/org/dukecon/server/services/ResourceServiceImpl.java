package org.dukecon.server.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dukecon.model.AbstractCoreImages;
import org.dukecon.model.Conference;
import org.dukecon.model.CoreImages;
import org.dukecon.model.Resources;
import org.dukecon.model.Speaker;
import org.dukecon.model.Styles;
import org.dukecon.server.conference.SpeakerImageService;
import org.dukecon.services.ConferenceService;
import org.dukecon.services.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by christoferdutz on 24.08.16.
 */
@Service("resourceService")
public class ResourceServiceImpl implements ResourceService {

    private static final String CLASSPATH_PUBLIC_IMG = "classpath:public/img";

    private final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Inject
    private ConferenceService conferenceService;

    @Inject
    private SpeakerImageService speakerImageService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public Map<String, byte[]> getLogosForConferences() {
        Map<String, byte[]> result = new HashMap<>();
        try {
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                    .getResources(CLASSPATH_PUBLIC_IMG + "/*");
            for (Resource resource : resources) {
                String conferenceId = resource.getFilename();
                byte[] logo = getImagesOrEmptyMap(conferenceId, "/conference").getOrDefault("logo", null);
                result.put(conferenceId,logo);
            }
        } catch (IOException e) {
            logger.info("No " + CLASSPATH_PUBLIC_IMG + " folder found. (" + e.getMessage() + ").");
        }

        return result;
    }

    @Override
    public Resources getResourcesForConference(String conferenceId) {
        Resources result = Resources.builder().build();

        // Styles
        Styles styles = conferenceService.getConferenceStyles(conferenceId);
        if (styles != null) {
            result.setStyles(styles);
        }

        Conference conference = conferenceService.read(conferenceId);
        addCoreImages(conferenceId, result);

        // Speakers
        result.setSpeakerImages(new HashMap<>());
        for (Speaker speaker : conference.getSpeakers()) {
            SpeakerImageService.ImageWithName image = speakerImageService.getImage(speaker.getPhotoId());
            if (image != null) {
                result.getSpeakerImages().put(speaker.getId(), image.getContent());
            }
        }

        return result;
    }

    @Override
    public CoreImages getCoreImagesForConference(String conferenceId) {
        CoreImages result = CoreImages.builder().build();

        addCoreImages(conferenceId, result);

        return result;

    }

    private void addCoreImages(String conferenceId, AbstractCoreImages result) {
        result.setConferenceImage(getImagesOrEmptyMap(conferenceId,"/conference").getOrDefault("logo",null));
        result.setConferenceFavIcon(getImagesOrEmptyMap(conferenceId,"/favicon").getOrDefault("favicon", null));
        result.setLocationImages(getImagesOrEmptyMap(conferenceId,"/locations"));
        result.setLocationMapImages(getImagesOrEmptyMap(conferenceId,"/location-maps"));
        result.setLanguageImages(getImagesOrEmptyMap(conferenceId,"/languages"));
        result.setStreamImages(getImagesOrEmptyMap(conferenceId,"/streams"));
    }

    private Map<String, byte[]> getImagesOrEmptyMap(String conferenceId, String path) {
        Map<String, byte[]> result = new HashMap<>();
        try {
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                    .getResources(CLASSPATH_PUBLIC_IMG + "/" + conferenceId + path + "/*.*");
            for (Resource resource : resources) {
                URL url = resource.getURL();
                String fileName = resource.getFilename();
                byte[] byteArray = IOUtils.toByteArray(url);
                String nameAsKey = StringUtils.substringBefore(fileName, ".");
                result.put(nameAsKey, byteArray);
            }
        } catch(IOException e) {
            logger.info("No path " + path + " for conference " + conferenceId + " found. (" + e.getMessage() + ").");
        }
        return result;
    }

}
