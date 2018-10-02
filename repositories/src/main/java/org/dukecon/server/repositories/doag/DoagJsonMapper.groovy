package org.dukecon.server.repositories.doag

import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.util.logging.Slf4j
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.repositories.RawDataMapper
import org.dukecon.server.repositories.RawDataResources

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
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
        parseResources(rawDataResources).each { k, v ->
            this.rawData[k] = v
        }
    }

    private Map<String, List> parseResources(RawDataResources rawDataResources) {
        return rawDataResources.get().collectEntries { k, v -> [(k): parseResource(v)] } as Map<String, List>
    }

    /**
     * @param wrapper resource
     * @return list of JSON objects/datasets
     */
    @TypeChecked(TypeCheckingMode.SKIP)
    private List parseResource(ResourceWrapper wrapper) {
        try {
            return new JsonSlurper().parse(wrapper.getStream() ?: new ByteArrayInputStream("{}".getBytes()), "ISO-8859-1")?.hits?.hits?._source
        } catch (Exception e) {
            log.error("Could not parse inputstream " + wrapper.name(), e)
            return []
        }
    }

    @Override
    @TypeChecked(TypeCheckingMode.SKIP)
    void useBackup(ResourceWrapper resourceWrapper) {
        this.rawData.clear()
        new JsonSlurper().parse(resourceWrapper.getStream(), "UTF-8").each { k, v ->
            this.rawData[k] = v
        }
    }

    @Override
    Map<String, Object> asMap() {
        return rawData
    }
}
