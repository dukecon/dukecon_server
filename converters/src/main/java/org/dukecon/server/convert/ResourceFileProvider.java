package org.dukecon.server.convert;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
public interface ResourceFileProvider<T> {
    String getFileName();
    T getContent();
}
