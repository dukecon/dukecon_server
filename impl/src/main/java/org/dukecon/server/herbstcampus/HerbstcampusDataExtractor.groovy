package org.dukecon.server.herbstcampus

import com.xlson.groovycsv.CsvIterator
import com.xlson.groovycsv.PropertyMapper
import org.dukecon.model.Audience
import org.dukecon.model.Conference
import org.dukecon.model.EventType
import org.dukecon.model.Language
import org.dukecon.model.Location
import org.dukecon.model.MetaData
import org.dukecon.model.Track
import org.dukecon.server.conference.ConferenceDataExtractor

/**
 * Extracts conference data from CSV file, this version can be used with data from 2016 onwards.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusDataExtractor implements ConferenceDataExtractor {

    private final String conferenceId
    private final List<PropertyMapper> input
    private final String conferenceUrl
    private final String conferenceName

    HerbstcampusDataExtractor(String conferenceId, CsvIterator input, String conferenceUrl = 'http://dukecon.org', String conferenceName = 'DukeCon Conference') {
        this.conferenceId = conferenceId
        this.input = input.findAll {PropertyMapper row -> !row.values[0].isEmpty()}
        this.conferenceName = conferenceName
        this.conferenceUrl = conferenceUrl
    }

    @Override
    Conference buildConference() {
        Conference conf = Conference.builder()
                .id(conferenceId)
                .name(conferenceName)
                .url(conferenceUrl)
                .metaData(metaData)
//                .speakers(this.speakers)
//                .events(this.events)
                .build()
    }

    private MetaData getMetaData() {
        MetaData.builder()
                .locations(this.locations)
                .tracks(this.tracks)
                .languages(this.languages)
                .defaultLanguage(this.defaultLanguage)
                .audiences(this.audiences)
                .eventTypes(this.eventTypes)
                .defaultIcon("Unknown.png")
                .build()
    }

    private List<Track> getTracks() {
        return input.collect {it.Kategorie}
                .unique()
                .findAll {it}
                .sort()
                .withIndex()
                .collect {track, index ->
                    Track.builder()
                            .id(index + 1 as String)
                            .order(index + 1)
                            .names([de: track, en: track])
                            .icon("track_${track}.png")
                            .build()
                }
    }

    private List<Language> getLanguages() {
        [Language.builder().id("1").code('de').order(1).names([de: 'Deutsch', en: 'German']).icon("language_de.png").build()]
    }

    private Language getDefaultLanguage() {
        return languages.first()
    }


    private List<Audience> getAudiences() {
        return input.collect {it.Level}
                .unique()
                .findAll {it}
                .sort()
                .withIndex()
                .collect {audience, index ->
                    Audience.builder()
                            .id(index + 1 as String)
                            .icon("audience_${index + 1}.png")
                            .order(index + 1)
                            .names([de: audience, en: audience])
                            .build()
        }
    }

    private List<EventType> getEventTypes() {
        return input.collect {it.Art}
                .unique()
                .findAll {it}
                .sort()
                .withIndex()
                .collect {eventType, index ->
            EventType.builder()
                    .id(index + 1 as String)
                    .icon("eventType_${index + 1}.png")
                    .order(index + 1)
                    .names([de: eventType, en: eventType])
                    .build()
        }
    }

    private List<Location> getLocations() {
        return input.collect {it.Raum}
                .unique()
                .findAll {it}
                .sort()
                .withIndex()
                .collect {room, index ->
            EventType.builder()
                    .id(index + 1 as String)
                    .icon("location_${index + 1}.png")
                    .order(index + 1)
                    .names([de: room, en: room])
                    .build()
        }
    }

}
