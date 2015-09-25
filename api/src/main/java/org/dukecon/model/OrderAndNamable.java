package org.dukecon.model;

import java.util.Map;

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
public interface OrderAndNamable {
    Map<Language, String> getNames();
    Integer getOrder();

    default String getName(String language) {
        return getNames().get(language);
    }

}
