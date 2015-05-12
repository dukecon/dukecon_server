package org.dukecon.server.impl

import groovy.transform.TypeChecked

import org.dukecon.server.impl.resource.MetaImpl
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.stereotype.Component

/**
 * @author ascheman
 *
 */
@Component
@TypeChecked
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(MetaImpl)
    }

}