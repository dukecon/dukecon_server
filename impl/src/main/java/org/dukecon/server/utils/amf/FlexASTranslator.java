package org.dukecon.server.utils.amf;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by christoferdutz on 12.08.16.
 */
public class FlexASTranslator extends flex.messaging.io.amf.translator.ASTranslator {

    @Override
    public Object convert(Object originalValue, Class type) {
        if(type.equals(LocalDateTime.class)) {
            return Date.from(((LocalDateTime) originalValue).atZone(ZoneId.systemDefault()).toInstant());
        }
        return super.convert(originalValue, type);
    }

}
