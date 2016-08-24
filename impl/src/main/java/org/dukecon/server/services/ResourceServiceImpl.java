package org.dukecon.server.services;

import org.apache.commons.io.IOUtils;
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

        Set<String> imageResources = servletContext.getResourcePaths("/public/img/");

        if(imageResources != null) {
            for (String imageResource : imageResources) {
                // Languages
                if (imageResource.startsWith("language_")) {
                    if (!result.containsKey("languages")) {
                        result.put("languages", new HashMap<>());
                    }
                    String id = imageResource.substring("language_".length());
                    byte[] imageData = getImageData(imageResource);
                    if (imageData != null) {
                        result.get("languages").put(id, imageData);
                    }
                }

                // Streams
                else if (imageResource.startsWith("track_")) {
                    if (!result.containsKey("streams")) {
                        result.put("streams", new HashMap<>());
                    }
                    String id = imageResource.substring("track_".length());
                    byte[] imageData = getImageData(imageResource);
                    if (imageData != null) {
                        result.get("streams").put(id, imageData);
                    }
                }
            }
        }

        return result;
    }

    private byte[] getImageData(String imageResource) {
        try {
            URL resourceUrl = servletContext.getResource(imageResource);
            InputStream resourceInputStream = resourceUrl.openStream();
            return IOUtils.toByteArray(resourceInputStream);
        } catch (MalformedURLException e) {
            // Not much we can do about this here ... simply ignore.
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
