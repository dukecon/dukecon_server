package org.dukecon.server.utils;

import flex.messaging.io.BeanProxy;
import flex.messaging.io.PropertyProxyRegistry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by christoferdutz on 04.08.16.
 */
public class LocalDateTimePropertyProxy extends BeanProxy {

    public LocalDateTimePropertyProxy() {
        super();
    }

    /////////////////////////////////////////////
    // Serialization
    /////////////////////////////////////////////

    @Override
    public String getAlias(final Object instance) {
        return "org.dukecon.model.LocalDateTime";
    }

    @Override
    public List getPropertyNames(final Object instance) {
        final List<String> propertyNames = new ArrayList<String>(2);
        propertyNames.add("date");
        propertyNames.add("time");
        return propertyNames;
    }

    @Override
    public Class getType(final Object instance, final String propertyName) {
        if ("date".equals(propertyName)) {
            return String.class;
        }
        if ("time".endsWith(propertyName)) {
            return String.class;
        }
        return null;
    }

    @Override
    public Object getValue(final Object instance, final String propertyName) {
        if ("date".equals(propertyName) && instance instanceof LocalDateTime) {
            final LocalDateTime localDateTime = (LocalDateTime) instance;
            return localDateTime.toLocalDate().toString();
        }
        if ("time".equals(propertyName) && instance instanceof LocalDateTime) {
            final LocalDateTime localDateTime = (LocalDateTime) instance;
            return localDateTime.toLocalTime().toString();
        }
        return null;
    }


    /////////////////////////////////////////////
    // Deserialization
    /////////////////////////////////////////////

    @Override
    public Object createInstance(String className) {
        return new HashMap<String, String>(2);
    }

    @Override
    public void setValue(Object instance, String propertyName, Object value) {
        @SuppressWarnings("unchecked")
        final Map<String, String> tempInstance = (Map<String, String>) instance;
        tempInstance.put(propertyName, value.toString());
    }

    @Override
    public Object instanceComplete(Object instance) {
        @SuppressWarnings("unchecked")
        final Map<String, String> tempInstance = (Map<String, String>) instance;
        final String dateString = tempInstance.get("date");
        final String timeString = tempInstance.get("time");
        return null;
        //return new LocalDateTime(new LocalDate(), new LocalTime());
    }


    /////////////////////////////////////////////
    // Registration
    /////////////////////////////////////////////

    static public LocalDateTimePropertyProxy registerPropertyProxy() {
        PropertyProxyRegistry.getRegistry().register(
                LocalDateTime.class, new LocalDateTimePropertyProxy()
        );

        // Sort of stupid, just to prevent NPEs in Spring.
        return new LocalDateTimePropertyProxy();
    }
}
