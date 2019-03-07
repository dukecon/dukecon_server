package org.dukecon.server.repositories.doag

import com.xlson.groovycsv.CsvParser
import groovy.json.JsonSlurper
import org.dukecon.model.Speaker
import org.dukecon.server.repositories.doag.DoagDataExtractor
import org.dukecon.server.repositories.doag.DoagSingleSpeakerMapper
import org.dukecon.server.repositories.doag.DoagSpeakersMapper
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagSingleSpeakerMapperSpec extends Specification {

    void "should read single speaker"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172,
            "NAME":"Jérôme Boateng",
            "VORNAME":"Jérôme",
            "NACHNAME":"Boateng",
            "WEBSEITE":"http://jeromeboateng.com/?",
            "FIRMA":null,
            "PROFILFOTO":null,
            "PROFILTEXT":"Alle Jahre wieder\r\nkommt das Christuskind\r\nauf die Erde nieder,\r\nwo wir Menschen sind.\r\nKehrt mit seinem Segen\r\nein in jedes Haus,\r\ngeht auf allen Wegen\r\nmit uns ein und aus.\r\nIst auch mir zur Seite\r\nstill und unerkannt,\r\ndaß es treu mich leite\r\nan der lieben Hand.",
            "LINKFACEBOOK":"https://www.facebook.com/JeromeBoateng17/",
            "LINKTWITTER":"https://twitter.com/JB17Official",
            "LINKXING":null,
            "LINKEDIN":null}''')
        then:
        json.ID_PERSON == 374172

        when:
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        singleSpeakerMapper.speaker.id == "374172"
        singleSpeakerMapper.speaker.name == "Jérôme Boateng"
        singleSpeakerMapper.speaker.website == "http://jeromeboateng.com/?"
        !singleSpeakerMapper.speaker.company
        singleSpeakerMapper.speaker.bio.startsWith('Alle Jahre wieder')
        singleSpeakerMapper.speaker.facebook == "https://www.facebook.com/JeromeBoateng17/"
        singleSpeakerMapper.speaker.twitter == "https://twitter.com/JB17Official"
        !singleSpeakerMapper.speaker.xing
        !singleSpeakerMapper.speaker.linkedin
        !singleSpeakerMapper.speaker.email
        !singleSpeakerMapper.speaker.gplus
    }

    void "should return correct field names by referent type"() {
        expect:
        DoagSingleSpeakerMapper.Type.DEFAULT.idKey == 'ID_PERSON'
        DoagSingleSpeakerMapper.Type.DEFAULT.nameKey == 'NAME'
        DoagSingleSpeakerMapper.Type.DEFAULT.lastnameKey == 'NACHNAME'
        DoagSingleSpeakerMapper.Type.DEFAULT.companyKey == 'FIRMA'

        DoagSingleSpeakerMapper.Type.REFERENT.idKey == 'ID_PERSON'
        DoagSingleSpeakerMapper.Type.REFERENT.nameKey == 'REFERENT_NAME'
        DoagSingleSpeakerMapper.Type.REFERENT.lastnameKey == 'REFERENT_NACHNAME'
        DoagSingleSpeakerMapper.Type.REFERENT.companyKey == 'REFERENT_FIRMA'

        DoagSingleSpeakerMapper.Type.COREFERENT.idKey == 'ID_PERSON_COREF'
        DoagSingleSpeakerMapper.Type.COREFERENT.nameKey == 'COREFERENT_NAME'
        DoagSingleSpeakerMapper.Type.COREFERENT.lastnameKey == 'COREFERENT_NACHNAME'
        DoagSingleSpeakerMapper.Type.COREFERENT.companyKey == 'COREFERENT_FIRMA'

        DoagSingleSpeakerMapper.Type.COCOREFERENT.idKey == 'ID_PERSON_COCOREF'
        DoagSingleSpeakerMapper.Type.COCOREFERENT.nameKey == 'COCOREFERENT_NAME'
        DoagSingleSpeakerMapper.Type.COCOREFERENT.lastnameKey == 'COCOREFERENT_NACHNAME'
        DoagSingleSpeakerMapper.Type.COCOREFERENT.companyKey == 'COCOREFERENT_FIRMA'
    }

    void "should concat first and lastname"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172,
            "VORNAME":"Jérôme",
            "NACHNAME":"Boateng"}''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        singleSpeakerMapper.speaker.firstname == "Jérôme"
        singleSpeakerMapper.speaker.lastname == "Boateng"
        singleSpeakerMapper.speaker.name == "Jérôme Boateng"
    }

    void "should read csv speaker data from JFS with and without co referent()"() {
        given:
        // CSV structure is same es JSON
        def csv = new CsvParser().parse('''REFERENT_NAME,REFERENT_NACHNAME,ID_PERSON,ID_PERSON_COREF,COREFERENT_NAME
Michael Plöd,Plöd,371278,,
Michael Vögeli,Vögeli,373808,373810,Ulrich Vigenschow,,
''').collect {line -> line.toMap()}

        when:
        def firstReferent = new DoagSingleSpeakerMapper(csv.first(), DoagSingleSpeakerMapper.Type.REFERENT)
        then:
        firstReferent.speaker.firstname == "Michael"
        firstReferent.speaker.lastname == "Plöd"
        firstReferent.speaker.name == "Michael Plöd"

        when:
        def firstCoReferent = new DoagSingleSpeakerMapper(csv.first(), DoagSingleSpeakerMapper.Type.COREFERENT)
        then:
        !firstCoReferent.speaker

        when:
        def secondReferent = new DoagSingleSpeakerMapper(csv[1], DoagSingleSpeakerMapper.Type.REFERENT)
        then:
        secondReferent.speaker.firstname == "Michael"
        secondReferent.speaker.lastname == "Vögeli"
        secondReferent.speaker.name == "Michael Vögeli"

        when:
        def secondCoReferent = new DoagSingleSpeakerMapper(csv[1], DoagSingleSpeakerMapper.Type.COREFERENT)
        then:
        secondCoReferent.speaker.firstname == "Ulrich"
        secondCoReferent.speaker.lastname == "Vigenschow"
        secondCoReferent.speaker.name == "Ulrich Vigenschow"
    }

    void "should read events without referent and co-referent"() {
        given:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":"",
            "REFERENT_NAME":""},
            "REFERENT_NACHNAME":""},
            "ID_PERSON_COREF":""},
            "COREFERENT_NAME":""}''')

        when:
        def referent = new DoagSingleSpeakerMapper(json, DoagSingleSpeakerMapper.Type.REFERENT)

        then:
        !referent.speaker;

        when:
        def coreferent = new DoagSingleSpeakerMapper(json, DoagSingleSpeakerMapper.Type.COREFERENT)

        then:
        !coreferent.speaker;

    }

        void "should split name in first and last"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172,
            "NAME":"Jérôme Boateng"}''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        singleSpeakerMapper.speaker.firstname == "Jérôme"
        singleSpeakerMapper.speaker.lastname == "Boateng"
        singleSpeakerMapper.speaker.name == "Jérôme Boateng"
    }

    void "should split name in several first and one last"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172,
            "NAME":"Jérôme Max Boateng"}''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        singleSpeakerMapper.speaker.firstname == "Jérôme Max"
        singleSpeakerMapper.speaker.lastname == "Boateng"
        singleSpeakerMapper.speaker.name == "Jérôme Max Boateng"

        when:
        singleSpeakerMapper = new DoagSingleSpeakerMapper(new JsonSlurper().parseText('''{
            "ID_PERSON" : 374172,
            "NAME" : "Hubert Klein Ikkink"}'''))
        then:
        singleSpeakerMapper.speaker.firstname == 'Hubert Klein'
        singleSpeakerMapper.speaker.lastname == 'Ikkink'
        singleSpeakerMapper.speaker.name == 'Hubert Klein Ikkink'

        when:
        singleSpeakerMapper = new DoagSingleSpeakerMapper(new JsonSlurper().parseText('''{
            "ID_PERSON" : 374172,
            "NAME" : "Hubert Klein Ikkink",
            "NACHNAME" : "Klein Ikkink"}'''))
        then:
        singleSpeakerMapper.speaker.firstname == 'Hubert'
        singleSpeakerMapper.speaker.lastname == 'Klein Ikkink'
        singleSpeakerMapper.speaker.name == 'Hubert Klein Ikkink'

        when:
        singleSpeakerMapper = new DoagSingleSpeakerMapper(new JsonSlurper().parseText('''{
            "ID_PERSON" : 374172,
            "NAME" : "Jan Carsten Lohmüller"}'''))
        then:
        singleSpeakerMapper.speaker.firstname == 'Jan Carsten'
        singleSpeakerMapper.speaker.lastname == 'Lohmüller'
        singleSpeakerMapper.speaker.name == 'Jan Carsten Lohmüller'
    }

    void "should not split nor concat first and lastname"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON":374172}''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json)
        then:
        !singleSpeakerMapper.speaker.firstname
        !singleSpeakerMapper.speaker.lastname
        !singleSpeakerMapper.speaker.name
    }

    void "should map first and lastname or split name from main referent"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON" : 368455,
            "REFERENT_NAME" : "Andrey Adamovich",
            "REFERENT_NACHNAME" : "Adamovich",
            "REFERENT_FIRMA" : "Aestas/IT",
        }''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json, DoagSingleSpeakerMapper.Type.REFERENT)
        then:
        singleSpeakerMapper.speaker.id == '368455'
        singleSpeakerMapper.speaker.name == 'Andrey Adamovich'
        singleSpeakerMapper.speaker.firstname == 'Andrey'
        singleSpeakerMapper.speaker.lastname == 'Adamovich'
        singleSpeakerMapper.speaker.company == 'Aestas/IT'
    }

    void "should map first and lastname or split name from co referent"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON_COREF" : 371991,
            "COREFERENT_NAME" : "Reinier Zwitserloot",
            "COREFERENT_FIRMA" : "medipc.nl",
        }''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json, DoagSingleSpeakerMapper.Type.COREFERENT)
        then:
        singleSpeakerMapper.speaker.id == '371991'
        singleSpeakerMapper.speaker.name == 'Reinier Zwitserloot'
        singleSpeakerMapper.speaker.firstname == 'Reinier'
        singleSpeakerMapper.speaker.lastname == 'Zwitserloot'
        singleSpeakerMapper.speaker.company == 'medipc.nl'
    }

    void "should map first and lastname or split name from co co referent"() {
        when:
        def json = new JsonSlurper().parseText('''{
            "ID_PERSON_COCOREF" : 371991,
            "COCOREFERENT_NAME" : "Reinier Zwitserloot",
            "COCOREFERENT_FIRMA" : "medipc.nl",
        }''')
        def singleSpeakerMapper = new DoagSingleSpeakerMapper(json, DoagSingleSpeakerMapper.Type.COCOREFERENT)
        then:
        singleSpeakerMapper.speaker.id == '371991'
        singleSpeakerMapper.speaker.name == 'Reinier Zwitserloot'
        singleSpeakerMapper.speaker.firstname == 'Reinier'
        singleSpeakerMapper.speaker.lastname == 'Zwitserloot'
        singleSpeakerMapper.speaker.company == 'medipc.nl'
    }

}