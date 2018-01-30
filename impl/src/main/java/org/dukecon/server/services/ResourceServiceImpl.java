package org.dukecon.server.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dukecon.model.*;
import org.dukecon.server.conference.SpeakerImageService;
import org.dukecon.services.ConferenceService;
import org.dukecon.services.ResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by christoferdutz on 24.08.16.
 */
@Service("resourceService")
public class ResourceServiceImpl implements ResourceService {

    @Value("${servlet.resource.dir:/WEB-INF/classes/public/img/}")
    private String resourceDir = "/WEB-INF/classes/public/img/";

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
        addCoreImages(conference, conferenceId, result, true);

        return result;

    }

    private void addCoreImages(Conference conference, String conferenceId, AbstractCoreImages result) {
        addCoreImages(conference, conferenceId, result, false);
    }

    private void addCoreImages(Conference conference, String conferenceId, AbstractCoreImages result, boolean base64) {
        // Logo
        Set<String> imageResources = servletContext.getResourcePaths(resourceDir + conferenceId + "/conference");
        if ((imageResources != null) && !imageResources.isEmpty()) {
            Map<String, byte[]> imageData = getImageData(imageResources);
            result.setConferenceImage(imageData.get("logo"));
        }

        // FavIcon
        imageResources = servletContext.getResourcePaths(resourceDir + conferenceId + "/favicon");
        if ((imageResources != null) && !imageResources.isEmpty()) {
            Map<String, byte[]> imageData = getImageData(imageResources);
            result.setConferenceFavIcon(imageData.get("favicon"));
        }

        // Locations
        imageResources = servletContext.getResourcePaths(resourceDir + conferenceId + "/locations");
        if ((imageResources != null) && !imageResources.isEmpty()) {
            result.setLocationImages(getImageData(imageResources));
        }

        // Location Maps
        imageResources = servletContext.getResourcePaths(resourceDir + conferenceId + "/location-maps");
        if ((imageResources != null) && !imageResources.isEmpty()) {
            result.setLocationMapImages(getImageData(imageResources));
        }

        // Languages
        imageResources = servletContext.getResourcePaths(resourceDir + conferenceId + "/languages");
        if ((imageResources != null) && !imageResources.isEmpty()) {
            result.setLanguageImages(getImageData(imageResources));
        }

        // Streams
        imageResources = servletContext.getResourcePaths(resourceDir + conferenceId + "/streams");
        if ((imageResources != null) && !imageResources.isEmpty()) {
            result.setStreamImages(getImageData(imageResources));
        }
    }
}
