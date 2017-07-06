package org.dukecon.model;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
public class CoreImagesSerializerTests {

    private CoreImagesSerializer coreImagesSerializer;
    private CoreImages coreImages;

    @Before
    public void init() {
        coreImagesSerializer = new CoreImagesSerializer();
        coreImages = new CoreImages();
        coreImages.setConferenceImage("dummylogo".getBytes());
        coreImages.setConferenceFavIcon("dummyfavicon".getBytes());
        final Map<String, byte[]> streamImages = new HashMap<>();
        streamImages.put("1", "streamicon1".getBytes());
        streamImages.put("2", "streamicon2".getBytes());
        streamImages.put("3", "streamicon3".getBytes());
        coreImages.setStreamImages(streamImages);
    }

    @Test
    public void testSerialization() throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator =
                new JsonFactory().createGenerator(stringWriter);
        coreImagesSerializer.serialize(coreImages, jsonGenerator, null);
        jsonGenerator.flush();
        String string = stringWriter.toString(); // string is ""
        assertNotNull(string);
        assertEquals("{\"conferenceImage\":\"data:image/png;base64,ZHVtbXlsb2dv\",\"conferenceFavIcon\":\"data:image/ico;base64,ZHVtbXlmYXZpY29u\",\"streamImages\":{\"1\":\"data:image/png;base64,c3RyZWFtaWNvbjE=\",\"2\":\"data:image/png;base64,c3RyZWFtaWNvbjI=\",\"3\":\"data:image/png;base64,c3RyZWFtaWNvbjM=\"}}", string);
        stringWriter.close();
    }
}
