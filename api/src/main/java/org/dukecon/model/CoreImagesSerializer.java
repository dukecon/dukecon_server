package org.dukecon.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Base64;

/**
 * Created by ascheman on 27.05.17.
 */
public class CoreImagesSerializer extends StdSerializer<CoreImages> {

    public CoreImagesSerializer() {
        this(null);
    }

    public CoreImagesSerializer(Class<CoreImages> t) {
        super(t);
    }

    @Override
    public void serialize(
            CoreImages value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();
        jgen.writeFieldName("conferenceImage");
        Base64.Encoder encoder = Base64.getEncoder();
        jgen.writeRawValue("\"data:image/png;base64," + new String (encoder.encode(value.getConferenceImage())) + "\"");
        // TODO Add other core images to serialization
        jgen.writeEndObject();
    }

}
