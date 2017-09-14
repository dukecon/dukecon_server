package org.dukecon.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * Created by ascheman on 27.05.17.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 * @author Gerd Aschemann, gerd@aschemann.net, @GerdAschemann
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

        Base64.Encoder encoder = Base64.getEncoder();
        jgen.writeStartObject();

        if (value.getConferenceImage() != null) {
            jgen.writeFieldName("conferenceImage");
            jgen.writeRawValue("\"data:image/png;base64," + new String(encoder.encode(value.getConferenceImage())) + "\"");
        }

        if (value.getConferenceFavIcon() != null) {
            jgen.writeFieldName("conferenceFavIcon");
            jgen.writeRawValue("\"data:image/ico;base64," + new String(encoder.encode(value.getConferenceFavIcon())) + "\"");
        }

        if (value.getStreamImages() != null && !value.getStreamImages().isEmpty()) {
            jgen.writeObjectFieldStart("streamImages");
            for (Map.Entry<String, byte[]> entry :
                    value.getStreamImages().entrySet()) {
                jgen.writeFieldName(entry.getKey());
                jgen.writeRawValue("\"data:image/png;base64," + new String(encoder.encode(entry.getValue())) + "\"");
            }
            jgen.writeEndObject();
        }

        // TODO Add other core images to serialization
        jgen.writeEndObject();
    }

}
