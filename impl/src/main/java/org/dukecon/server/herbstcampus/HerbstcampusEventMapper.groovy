package org.dukecon.server.herbstcampus

import org.dukecon.model.Event
import org.dukecon.model.Speaker

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusEventMapper {

    final List<Event> events = []
    private String startDate
    private HerbstcampusSpeakerMapper speakerMapper
    private HerbstcampusLanguageMapper languageMapper
    private HerbstcampusStreamMapper streamMapper
    private HerbstcampusAudienceMapper audienceMapper
    private HerbstcampusEventTypeMapper eventTypeMapper
    private HerbstcampusLocationMapper locationMapper

    HerbstcampusEventMapper(input, String startDate, HerbstcampusSpeakerMapper speakerMapper, HerbstcampusLanguageMapper languageMapper, HerbstcampusStreamMapper streamMapper, HerbstcampusAudienceMapper audienceMapper, HerbstcampusEventTypeMapper eventTypeMapper, HerbstcampusLocationMapper locationMapper) {
        this.startDate = startDate
        this.speakerMapper = speakerMapper
        this.streamMapper = streamMapper
        this.languageMapper = languageMapper
        this.locationMapper = locationMapper
        this.eventTypeMapper = eventTypeMapper
        this.audienceMapper = audienceMapper
        input.each { row ->
            this.events << getEvent(row)
        }
    }

    private Event getEvent(row) {
        def speakers = speakerMapper.getSpeakersForEvent(row.values[0])
        def event = Event.builder()
                .id(row.values[0])
                .title("${row.Titel} - ${row.Untertitel}")
                .abstractText(row.Kurzabstract)
                .start(getTime(row, startDate, row.Beginn))
                .end(getTime(row, startDate, row.Ende))
                .language(languageMapper.defaultLanguage)
                .track(streamMapper.entityForName((row.Kategorie)))
                .audience(audienceMapper.entityForName(row.Level))
                .type(eventTypeMapper.entityForName(row.Art))
                .location(locationMapper.entityForName(row.Raum))
                .speakers(speakers)
                .build()
        speakers.each {Speaker s ->
            s.events = s.events + event
        }
        return event
    }

    private LocalDateTime getTime(row, String date, String time) {
        switch (row.Tag) {
            case 3:
                return LocalDateTime.parse("${date} ${time}", DateTimeFormatter.ofPattern('yyyy-MM-dd HHmm'))
            default:
                return (LocalDateTime.parse("${date} ${time}", DateTimeFormatter.ofPattern('yyyy-MM-dd HHmm')).plusDays(Long.valueOf(row.Tag)))
        }
    }

}
