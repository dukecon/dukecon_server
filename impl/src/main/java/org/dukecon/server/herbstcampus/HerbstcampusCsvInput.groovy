package org.dukecon.server.herbstcampus

import com.xlson.groovycsv.PropertyMapper

import static com.xlson.groovycsv.CsvParser.parseCsv

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusCsvInput implements Iterable<PropertyMapper> {

    private List<PropertyMapper> input

    HerbstcampusCsvInput(String filename) {
        InputStream stream = HerbstcampusCsvInput.class.getResourceAsStream("/${filename}")
        this.input = tidyUpCsvIterator(parseCsv(new InputStreamReader(stream), separator: ';'))
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
}
