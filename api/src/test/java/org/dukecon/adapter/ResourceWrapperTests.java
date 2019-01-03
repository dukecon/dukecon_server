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
        assertEquals("URL: http://www.google.com", ResourceWrapper.of(new URL(GOOGLE_URL)).name());
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
            assertEquals("URL: http://doesnotexist", wrapper.name());
        }
    }

    @Test
    public void testWrapFromExistingFile() throws Exception {
        assertNotNull(ResourceWrapper.of(new File("pom.xml")).getStream());
        assertEquals("File: pom.xml", ResourceWrapper.of(new File("pom.xml")).name());
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
            assertEquals("File: notexists.txt", wrapper.name());
        }
    }

    @Test
    public void testWrapFromStringAsUrl() throws Exception {
        assertNotNull(ResourceWrapper.of(GOOGLE_URL).getStream());
        assertEquals("Resource: http://www.google.com", ResourceWrapper.of(GOOGLE_URL).name());
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
            assertEquals("Resource: http://doesnotexist", wrapper.name());
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
            assertEquals("Resource: file:notexists.txt", wrapper.name());
        }
    }

    @Test
    public void testWrapFromStringAsResourceAsStream() throws Exception {
        assertNotNull(ResourceWrapper.of("org/dukecon/model/Speaker.class").getStream());
        assertEquals("Resource: org/dukecon/model/Speaker.class", ResourceWrapper.of("org/dukecon/model/Speaker.class").name());
    }

    @Test
    public void testWrapFromClasspathFile() throws Exception {
        assertNotNull(ResourceWrapper.of("dummy.resource").getStream());
        assertEquals("Resource: dummy.resource", ResourceWrapper.of("dummy.resource").name());
    }
}
