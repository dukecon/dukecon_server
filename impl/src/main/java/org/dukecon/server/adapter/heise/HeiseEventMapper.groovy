package org.dukecon.server.adapter.heise

import org.dukecon.model.Event
import org.dukecon.model.Speaker

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseEventMapper {

    final List<Event> events = []
    private LocalDate startDate
    private HeiseSpeakerMapper speakerMapper
    private HeiseLanguageMapper languageMapper
    private HeiseStreamMapper streamMapper
    private HeiseAudienceMapper audienceMapper
    private HeiseEventTypeMapper eventTypeMapper
    private HeiseLocationMapper locationMapper

    HeiseEventMapper(input, LocalDate startDate, HeiseSpeakerMapper speakerMapper, HeiseLanguageMapper languageMapper, HeiseStreamMapper streamMapper, HeiseAudienceMapper audienceMapper, HeiseEventTypeMapper eventTypeMapper, HeiseLocationMapper locationMapper) {
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
        speakers.each { Speaker s ->
            s.events = s.events + event
        }
        return event
    }

    private LocalDateTime getTime(row, LocalDate date, String time) {
        switch (row.Tag) {
            case '3':
                return date.atTime(getHours(time), getMinutes(time))
            default:
                return date.atTime(getHours(time), getMinutes(time)).plusDays(Long.valueOf(row.Tag))
        }
    }

    private int getHours(String time) {
        Integer.valueOf(time[0..1])
    }

    private int getMinutes(String time) {
        Integer.valueOf(time[-2..-1])
    }
}
