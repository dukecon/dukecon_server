package org.dukecon.server.herbstcampus

import org.dukecon.model.Conference
import org.dukecon.server.conference.ConferenceDataExtractor

/**
 * Extracts conference data from CSV file, this version can be used with data from 2016 onwards.
 *
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusDataExtractor implements ConferenceDataExtractor {

    final Conference conference

    HerbstcampusDataExtractor(String conferenceId, input, String startDate, String conferenceUrl = 'http://dukecon.org', String conferenceName = 'DukeCon Conference') {
        HerbstcampusLocationMapper locationMapper = new HerbstcampusLocationMapper(input)
        HerbstcampusStreamMapper streamMapper = new HerbstcampusStreamMapper(input)
        HerbstcampusLanguageMapper languageMapper = new HerbstcampusLanguageMapper(input)
        HerbstcampusAudienceMapper audienceMapper = new HerbstcampusAudienceMapper(input)
        HerbstcampusEventTypeMapper eventTypeMapper = new HerbstcampusEventTypeMapper(input)
        HerbstcampusMetaDataMapper metaDataMapper = new HerbstcampusMetaDataMapper(streamMapper, locationMapper, languageMapper, audienceMapper, eventTypeMapper)
        HerbstcampusSpeakerMapper speakerMapper = new HerbstcampusSpeakerMapper(input)
        HerbstcampusEventMapper eventMapper = new HerbstcampusEventMapper(input, startDate, speakerMapper, languageMapper, streamMapper, audienceMapper, eventTypeMapper, locationMapper)
        this.conference = Conference.builder()
                .id(conferenceId)
                .name(conferenceName)
                .url(conferenceUrl)
                .metaData(metaDataMapper.metaData)
                .speakers(speakerMapper.speakers)
                .events(eventMapper.events)
                .build()
    }

}
