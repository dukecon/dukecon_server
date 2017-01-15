package org.dukecon.server.adapter.heise

import org.dukecon.model.Conference
import org.dukecon.server.adapter.ConferenceDataExtractor
import org.dukecon.server.adapter.RawDataMapper

import java.time.LocalDate

/**
 * Extracts conference data from CSV file, this version can be used with data from 2016 onwards.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseDataExtractor implements ConferenceDataExtractor {

    final Conference conference

    HeiseDataExtractor(String conferenceId, RawDataMapper rawDataMapper, LocalDate startDate, String conferenceName = 'DukeCon Conference', String conferenceUrl = 'http://dukecon.org') {
        this.conference = fromInput(getInput(rawDataMapper), startDate, conferenceId, conferenceName, conferenceUrl)
    }

    private def getInput(RawDataMapper rawDataMapper) {
        rawDataMapper instanceof InputStream ? new HeiseCsvInput(rawDataMapper) : rawDataMapper
    }

    private Conference fromInput(input, LocalDate startDate, String conferenceId, String conferenceName, String conferenceUrl) {
        HeiseLocationMapper locationMapper = new HeiseLocationMapper(input)
        HeiseStreamMapper streamMapper = new HeiseStreamMapper(input)
        HeiseLanguageMapper languageMapper = new HeiseLanguageMapper(input)
        HeiseAudienceMapper audienceMapper = new HeiseAudienceMapper(input)
        HeiseEventTypeMapper eventTypeMapper = new HeiseEventTypeMapper(input)
        HeiseMetaDataMapper metaDataMapper = new HeiseMetaDataMapper(streamMapper, locationMapper, languageMapper, audienceMapper, eventTypeMapper)
        HeiseSpeakerMapper speakerMapper = new HeiseSpeakerMapper(input)
        HeiseEventMapper eventMapper = new HeiseEventMapper(input, startDate, speakerMapper, languageMapper, streamMapper, audienceMapper, eventTypeMapper, locationMapper)
        Conference.builder()
                .id(conferenceId)
                .name(conferenceName)
                .url(conferenceUrl)
                .metaData(metaDataMapper.metaData)
                .speakers(speakerMapper.speakers)
                .events(eventMapper.events)
                .build()
    }

    @Override
    RawDataMapper getRawDataMapper() {
        return null
    }
}
