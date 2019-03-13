package org.dukecon.server.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dukecon.model.*;
import org.dukecon.server.conference.SpeakerImageService;
import org.dukecon.services.ConferenceService;
import org.dukecon.services.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by christoferdutz on 24.08.16.
 */
@Service("resourceService")
public class ResourceServiceImpl implements ResourceService {

    Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Inject
    private ServletContext servletContext;

    @Inject
    private ConferenceService conferenceService;

    @Inject
    private SpeakerImageService speakerImageService;

    @Override
    public Map<String, byte[]> getLogosForConferences() {
        Map<String, byte[]> result = new HashMap<>();
        Set<String> conferenceImageResources = servletContext.getResourcePaths("/public/img");
        for (String conferenceResourcesRoot : conferenceImageResources) {
            String conferenceId = StringUtils.substringAfterLast(
                    StringUtils.substringBeforeLast(conferenceResourcesRoot, "/"), "/");
            Set<String> conferenceResources = servletContext.getResourcePaths(conferenceResourcesRoot + "/conference");
            Map<String, byte[]> confResources = getImageData(conferenceResources);
            if (confResources.containsKey("logo")) {
                result.put(conferenceId, confResources.get("logo"));
            }
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

    private Map<String, byte[]> getImageData(Set<String> imageResources) {
        Map<String, byte[]> images = new HashMap<>();
        if (imageResources != null) {
            for (String imageResource : imageResources) {
                String id = StringUtils.substringBefore(StringUtils.substringAfterLast(imageResource, "/"), ".");
                try {
                    URL resourceUrl = servletContext.getResource(imageResource);
                    InputStream resourceInputStream = resourceUrl.openStream();
                    byte[] imageData = IOUtils.toByteArray(resourceInputStream);
                    if (imageData != null) {
                        images.put(id, imageData);
                    }
                } catch (MalformedURLException e) {
                    // Not much we can do about this here ... simply ignore.
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return images;
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
        result.setConferenceImage(getImagesOrEmptyMap(conferenceId,"/conference").getOrDefault("logo",null));
        result.setConferenceFavIcon(getImagesOrEmptyMap(conferenceId,"/favicon").getOrDefault("favicon", null));
        result.setLocationImages(getImagesOrEmptyMap(conferenceId,"/locations"));
        result.setLocationMapImages(getImagesOrEmptyMap(conferenceId,"/location-maps"));
        result.setLanguageImages(getImagesOrEmptyMap(conferenceId,"/languages"));
        result.setStreamImages(getImagesOrEmptyMap(conferenceId,"/streams"));

    }

    private Map<String,byte[]> getImagesOrEmptyMap(String conferenceId, String locations) {
        try {
            String resourceDir = "public/img/";
            File directory = ResourceUtils.getFile("classpath:" + resourceDir + conferenceId + locations);
            if (directory.exists()) {
                return getFilenameAsKeyToFilecontentBytesMap(directory);
            }
        } catch(IOException ioe) {
            logger.info("getImages failed with", ioe);
        }
        return new HashMap<>();
    }

    private Map<String, byte[]> getFilenameAsKeyToFilecontentBytesMap(File directory) throws IOException {
        Map<String, byte[]> result = new HashMap<>();
        for (String fileString : directory.list()) {
            File file = ResourceUtils.getFile(directory.getPath() + "/" + fileString);
            byte[] allBytes = Files.readAllBytes(file.toPath());
            result.put(StringUtils.substringBefore(file.getName(), "."), allBytes);
        }
        return result;
    }
}
