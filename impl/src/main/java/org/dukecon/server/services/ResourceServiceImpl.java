package org.dukecon.server.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dukecon.services.ResourceService;
import org.springframework.flex.remoting.RemotingDestination;
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
@RemotingDestination
public class ResourceServiceImpl implements ResourceService {

    @Inject
    private ServletContext servletContext;

    @Override
    public Map<String, Map<String, byte[]>> getResourcesForConference(String conferenceId) {
        Map<String, Map<String, byte[]>> result = new HashMap<>();

        // Languages
        Set<String> imageResources = servletContext.getResourcePaths("/public/img/languages");
        result.put("languages", getImageData(imageResources));

        // Streams
        imageResources = servletContext.getResourcePaths("/public/img/streams");
        result.put("streams", getImageData(imageResources));

        return result;
    }

    private Map<String, byte[]> getImageData(Set<String> imageResources) {
        Map<String, byte[]> images = new HashMap<>();
        if(imageResources != null) {
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

}
