package org.dukecon.server.utils.amf;

import flex.messaging.io.SerializationContext;

/**
 * Created by christoferdutz on 12.08.16.
 */
public class Amf0Output extends flex.messaging.io.amf.Amf0Output {

    public Amf0Output(SerializationContext context) {
        super(context);
    }

    protected void createAMF3Output() {
        avmPlusOutput = new Amf3Output(context);
        avmPlusOutput.setOutputStream(out);
        avmPlusOutput.setDebugTrace(trace);
    }

}
