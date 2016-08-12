package org.dukecon.server.utils.amf;

import flex.messaging.io.MessageIOConstants;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.AmfMessageSerializer;
import flex.messaging.io.amf.AmfTrace;

import java.io.OutputStream;

/**
 * Created by christoferdutz on 12.08.16.
 */
public class Serializer extends AmfMessageSerializer {

    @Override
    public void initialize(SerializationContext context, OutputStream out, AmfTrace trace) {
        amfOut = new Amf0Output(context);
        amfOut.setOutputStream(out);
        amfOut.setAvmPlus(version >= MessageIOConstants.AMF3);

        debugTrace = trace;
        isDebug = trace != null;
        amfOut.setDebugTrace(debugTrace);
    }

}
