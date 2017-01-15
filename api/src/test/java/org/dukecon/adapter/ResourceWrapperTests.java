package org.dukecon.adapter;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
public class ResourceWrapperTests {

    public static final String GOOGLE_URL = "http://www.google.com";
    public static final String DOESNOTEXIST_URL = "http://doesnotexist";

    @Test
    public void testWrapFromExistingUrl() throws IOException {
        assertNotNull(ResourceWrapper.of(new URL(GOOGLE_URL)).getStream());
    }

    @Test
    public void testWrapFromNotExistingUrl() throws IOException {
        ResourceWrapper wrapper = ResourceWrapper.of(new URL(DOESNOTEXIST_URL));
        assertNotNull(wrapper);
        try {
            wrapper.getStream();
            fail("may not create stream of not existing URL");
        } catch (IOException e) {
            assertEquals("doesnotexist", e.getMessage());
        }
    }

    @Test
    public void testWrapFromExistingFile() throws Exception {
        assertNotNull(ResourceWrapper.of(new File("pom.xml")).getStream());
    }

    @Test
    public void testWrapFromNotExistingFile() throws IOException {
        ResourceWrapper wrapper = ResourceWrapper.of(new File("notexists.txt"));
        assertNotNull(wrapper);
        try {
            wrapper.getStream();
            fail("may not create stream of not existing file");
        } catch (IOException e) {
            assertTrue(e.getMessage().startsWith("notexists.txt ("));
        }
    }

    @Test
    public void testWrapFromStringAsUrl() throws Exception {
        assertNotNull(ResourceWrapper.of(GOOGLE_URL).getStream());
    }

    @Test
    public void testWrapFromStringAsNotExistingUrl() throws Exception {
        ResourceWrapper wrapper = ResourceWrapper.of(DOESNOTEXIST_URL);
        assertNotNull(wrapper);
        try {
            wrapper.getStream();
            fail("may not create stream of not existing URL");
        } catch (IOException e) {
            assertEquals("doesnotexist", e.getMessage());
        }
    }

    @Test
    public void testWrapFromStringAsFile() throws Exception {
        assertNotNull(ResourceWrapper.of("file:pom.xml").getStream());
    }

    @Test
    public void testWrapFromStringAsNotExistingFile() throws Exception {
        ResourceWrapper wrapper = ResourceWrapper.of("file:notexists.txt");
        assertNotNull(wrapper);
        try {
            wrapper.getStream();
            fail("may not create stream of not existing file");
        } catch (IOException e) {
            assertTrue(e.getMessage().startsWith("notexists.txt ("));
        }
    }

    @Test
    public void testWrapFromStringAsResourceAsStream() throws Exception {
        assertNotNull(ResourceWrapper.of("org/dukecon/model/Speaker.class").getStream());
    }
}
