package org.dukecon.server.adapter.doag

import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.adapter.RawDataResources
import org.dukecon.server.adapter.RawDataMapper

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class DoagJsonMapper implements RawDataMapper {

    private final RawDataResources rawDataResources
    private final Map<String, Object> rawData = [:]

    DoagJsonMapper(RawDataResources rawDataResources) {
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
            [(k): new JsonSlurper().parse(v.getStream() ?: new byte[0], "ISO-8859-1").hits.hits._source]
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
