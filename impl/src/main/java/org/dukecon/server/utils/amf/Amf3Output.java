package org.dukecon.server.utils.amf;

import flex.messaging.io.SerializationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by christoferdutz on 12.08.16.
 */
public class Amf3Output extends flex.messaging.io.amf.Amf3Output {

    public Amf3Output(SerializationContext context) {
        super(context);
    }

    @Override
    public void writeObject(Object value) throws IOException {
        if(value instanceof LocalDateTime) {
            value = convertToDate((LocalDateTime)value);
        }
        super.writeObject(value);
    }

    private Object convertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
