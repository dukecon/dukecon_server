package org.dukecon.server.business

import groovy.json.JsonSlurper
import org.dukecon.model.MetaData
import spock.lang.Specification


/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class MetaDataLookupSpec extends Specification {
    private static MetaData metaData

    void setupSpec() {
        InputStream is = MetaDataExtractorSpec.class.getResourceAsStream('/javaland-2016.raw')
        JsonSlurper slurper = new JsonSlurper()
        def rawJson = slurper.parse(is, "ISO-8859-1")
        MetaDataExtractor extractor = new MetaDataExtractor(talksJson: rawJson.hits.hits._source)
        metaData = extractor.buildMetaData()
    }

    void "should extract order from room name"() {
        when:
        MetaDataLookup lookup = new MetaDataLookup(metaData)
        then:
        assert 1 == lookup.rooms['Wintergarten']
        assert 2 == lookup.rooms['Schauspielhaus']
    }

    void "should extract order from track name"() {
        when:
        MetaDataLookup lookup = new MetaDataLookup(metaData)
        then:
        assert 1 == lookup.tracks['Container & Microservices']
        'Container & Microservices, Core Java & JVM basierte Sprachen, Enterprise Java & Cloud, Frontend & Mobile, IDEs & Tools, Internet der Dinge, Architektur & Sicherheit, Newcomer'.tokenize(',')*.trim().eachWithIndex {e, i ->
            assert (i + 1) == lookup.tracks[e]
        }
    }
    void "should extract order from talk type name"() {
        when:
        MetaDataLookup lookup = new MetaDataLookup(metaData)
        then:
        'Best Practices, Keynote, Neuerscheinungen oder Features, Projektbericht, Tipps & Tricks'.tokenize(',')*.trim().eachWithIndex {e, i ->
            assert (i + 1) == lookup.talkTypes[e]
        }
    }

    void "should extract order from audience name"() {
        when:
        MetaDataLookup lookup = new MetaDataLookup(metaData)
        then:
        assert 1 == lookup.audiences['Anfänger']
        assert 2 == lookup.audiences['Fortgeschrittene']
        assert !lookup.audiences['']
        assert !lookup.audiences[null]
        assert !lookup.audiences['irgendwas']
    }
}