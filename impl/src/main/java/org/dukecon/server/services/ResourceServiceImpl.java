package org.dukecon.server.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dukecon.model.*;
import org.dukecon.server.conference.SpeakerImageService;
import org.dukecon.services.ConferenceService;
import org.dukecon.services.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by christoferdutz on 24.08.16.
 */
@Service("resourceService")
public class ResourceServiceImpl implements ResourceService {

    Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

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
            File imageDir = new ClassPathResource("classpath:public/img").getFile();
            if (imageDir.exists()) {
                for (String conferenceId : imageDir.list()) {
                    byte[] logo = getImagesOrEmptyMap(conferenceId, "/conference").getOrDefault("logo", null);
                    result.put(conferenceId,logo);
                }
            }
        } catch (Exception e) {
            logger.error("",e);
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
        addCoreImages(conference, conferenceId, result);

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

        Conference conference = conferenceService.read(conferenceId);
        try {
            addCoreImages(conference, conferenceId, result, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    private void addCoreImages(Conference conference, String conferenceId, AbstractCoreImages result) {
        try {
            addCoreImages(conference, conferenceId, result, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCoreImages(Conference conference, String conferenceId, AbstractCoreImages result, boolean base64) throws IOException {
        result.setConferenceImage(readBytesFromFile("public/img/" + conferenceId + "/conference/logo.png"));
        result.setConferenceFavIcon(readBytesFromFile("public/img/" + conferenceId + "/favicon/favicon.ico"));
        result.setLocationImages(getImagesOrEmptyMap(conferenceId,"/locations"));
        result.setLocationMapImages(getImagesOrEmptyMap(conferenceId,"/location-maps"));
        result.setLanguageImages(getImagesOrEmptyMap(conferenceId,"/languages"));
        result.setStreamImages(getImagesOrEmptyMap(conferenceId,"/streams"));
    }

    private Map<String, byte[]> getImagesOrEmptyMap(String conferenceId, String path) {
        Map<String, byte[]> result = new HashMap<>();
        String resourceDir = "public/img/";
        try {
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader)
                    .getResources("classpath:" + resourceDir + conferenceId + path + "/*.*");
            for (int i = 0; i < resources.length; i++) {
                URL url = resources[i].getURL();
                String fileName = resources[i].getFilename();
                byte[] byteArray = IOUtils.toByteArray(url);
                result.put(StringUtils.substringBefore(fileName, "."), byteArray);
            }
        } catch(IOException e) {
            //
        }
        return result;
    }

    private byte[] readBytesFromFile(String path) throws IOException {
        URL fileURL = new ClassPathResource(path).getURL();
        return IOUtils.toByteArray(fileURL);
    }
}
