package org.dukecon.server.repositories.apache

import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.repositories.RawDataMapper
import org.dukecon.server.repositories.RawDataResources

/**
 * @author Christofer Dutz, christofer.dutz@codecentric.de, @ChristoferDutz
 */
@TypeChecked
class ApacheJsonMapper implements RawDataMapper {

    private final RawDataResources rawDataResources
    private final Map<String, Object> rawData = [:]

    ApacheJsonMapper(RawDataResources rawDataResources) {
        this.rawDataResources = rawDataResources
    }

    @Override
    void initMapper() {
        this.rawData.clear()
        parseResources(rawDataResources).each {k, v ->
            this.rawData[k] = v
        }
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private Map<String, Object> parseResources(RawDataResources rawDataResources) {
        rawDataResources.get().collectEntries {k, v ->
            [(k): new JsonSlurper().parse(v.getStream() ?: new byte[0], "UTF-8")]
        }
    }

    @Override
    @TypeChecked(TypeCheckingMode.SKIP)
    void useBackup(ResourceWrapper resourceWrapper) {
        this.rawData.clear()
        new JsonSlurper().parse(resourceWrapper.getStream(), "UTF-8").each {k, v ->
            this.rawData[k] = v
        }
    }

    @Override
    Map<String, Object> asMap() {
        return rawData
    }
}
