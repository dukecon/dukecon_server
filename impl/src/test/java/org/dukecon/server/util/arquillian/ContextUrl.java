package org.dukecon.server.util.arquillian;

import java.net.MalformedURLException;
import java.net.URL;

public interface ContextUrl {
    URL url() throws MalformedURLException;
}
