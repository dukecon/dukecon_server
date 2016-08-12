package org.dukecon.server.utils.amf;

import flex.messaging.endpoints.AMFEndpoint;

/**
 * Created by christoferdutz on 12.08.16.
 */
public class AmfEndpoint extends AMFEndpoint {

    @Override
    protected String getSerializerClassName() {
        return Serializer.class.getName();
    }

}
