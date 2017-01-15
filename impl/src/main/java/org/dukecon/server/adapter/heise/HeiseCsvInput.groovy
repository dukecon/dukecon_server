package org.dukecon.server.adapter.heise

import com.xlson.groovycsv.PropertyMapper
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.server.adapter.RawDataMapper
import org.dukecon.server.adapter.RawDataResources

import static com.xlson.groovycsv.CsvParser.parseCsv

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseCsvInput implements Iterable<PropertyMapper>, RawDataMapper {

    private final List<PropertyMapper> input

    HeiseCsvInput(RawDataResources resources) {
        this(resources.get().eventsData.getStream())
    }

    private HeiseCsvInput(InputStream inputStream) {
        this.input = tidyUpCsvIterator(parseCsv(new InputStreamReader(inputStream, "UTF-8"), separator: ';'))
    }

    /**
     * Removes empty lines in CSV.
     *
     * @param input
     * @return
     */
    private List<PropertyMapper> tidyUpCsvIterator(input) {
        input.findAll { PropertyMapper row -> !row.values[0].isEmpty() }
    }

    @Override
    Iterator<PropertyMapper> iterator() {
        return input.iterator()
    }

    @Override
    Map<String, Object> asMap() {
        return [eventsData:input]
    }

    @Override
    void initMapper() {

    }

    @Override
    void useBackup(ResourceWrapper resourceWrapper) {
    }
}
