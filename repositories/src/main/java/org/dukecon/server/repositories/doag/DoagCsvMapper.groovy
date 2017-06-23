package org.dukecon.server.repositories.doag

import com.xlson.groovycsv.PropertyMapper
import groovy.json.JsonSlurper
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.repositories.RawDataMapper
import org.dukecon.server.repositories.RawDataResources

import static com.xlson.groovycsv.CsvParser.parseCsv

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
class DoagCsvMapper implements RawDataMapper {

    private final RawDataResources rawDataResources
    private final Map<String, Object> rawData = [:]

    DoagCsvMapper(RawDataResources rawDataResources) {
        this.rawDataResources = rawDataResources
    }

    @Override
    void initMapper() {
        this.rawData.clear()
        this.rawData.putAll(parseResources(rawDataResources))
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private Map<String, Object> parseResources(RawDataResources rawDataResources) {
        rawDataResources.get().collectEntries {k, v ->
            def csvLines = parseCsv(new InputStreamReader(v.getStream(), "ISO-8859-1"), separator: ',')
            return [(k): csvLines.collect { PropertyMapper line -> line.toMap() }]
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
