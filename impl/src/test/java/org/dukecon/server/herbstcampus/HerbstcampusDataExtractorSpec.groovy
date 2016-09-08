package org.dukecon.server.herbstcampus

import com.xlson.groovycsv.CsvIterator
import org.dukecon.model.Conference
import spock.lang.Specification
import static com.xlson.groovycsv.CsvParser.parseCsv

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack.
 */
class HerbstcampusDataExtractorSpec extends Specification {
    private static HerbstcampusDataExtractor extractor = new HerbstcampusDataExtractor('hc16', readCsv())
    private Conference conference

    void setup() {
        conference = extractor.buildConference()
    }

    private static CsvIterator readCsv() {
        parseCsv(new BufferedReader(new InputStreamReader(HerbstcampusDataExtractorSpec.class.getResourceAsStream('/herbstcampus_2016_veranstaltungen_20160826.csv'))), separator: ';')
    }

    void "should contain metadata"() {
        expect:
            conference.id == 'hc16'
            conference.name == 'DukeCon Conference'
            conference.url == 'http://dukecon.org'
    }

    void "should get 12 streams"() {
        when:
            def tracks = conference.metaData.tracks
        then:
            tracks.size() == 12
            tracks.names['de'] == ['01 Java', '02 .NET', '03 JavaScript', '04 andere Sprachen', '05 Architektur', '06 Testen/Qualität', '07 Infrastruktur', '08 Sicherheit', '09 Agile/Soft Skills', '10 Mobile', '11 Big Data/Search', '12 Diverses']
    }

    void "should get one language"() {
        when:
        def languages = conference.metaData.languages
        def defaultLanguage = conference.metaData.defaultLanguage
        then:
        languages.size() == 1
        languages.names['de'] == ['Deutsch']
        defaultLanguage.names['de'] == 'Deutsch'
    }

    void "should get 6 locations"() {
        when:
        def locations = conference.metaData.locations
        then:
        locations.size() == 6
        locations.names['de'] == ['H1', 'H2', 'H3', 'H4', 'H5', 'XX']
    }

    void "should get 3 event types"() {
        when:
        def eventTypes = conference.metaData.eventTypes
        then:
        eventTypes.size() == 3
        eventTypes.names['de'] == ['Keynote', 'Tutorium', 'Vortrag']
    }

    void "should get 3 audiences"() {
        when:
        def audiences = conference.metaData.audiences
        then:
        audiences.size() == 2
        audiences.names['de'] == ['Einsteiger', 'Experten']
    }




}
