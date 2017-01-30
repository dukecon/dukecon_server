package org.dukecon.server.adapter.doag

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import org.dukecon.model.Speaker
import org.dukecon.server.adapter.doag.DoagSingleSpeakerMapper.Type

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagSpeakersMapper {

    final Map<String, Speaker> speakers = [:]
    final List photos = []
    final Multimap<String, String> eventIds2SpeakerIds = HashMultimap.create()
    final Multimap<String, String> speakerIds2EventsIds = HashMultimap.create()

    static DoagSpeakersMapper createFrom(eventInput, speakerInput) {
        new DoagSpeakersMapper(eventInput, DoagSingleSpeakerMapper.Type.REFERENT)
            .addSpeakers(eventInput, DoagSingleSpeakerMapper.Type.COREFERENT)
            .addSpeakers(eventInput, DoagSingleSpeakerMapper.Type.COCOREFERENT)
            .addSpeakers(speakerInput)
    }

    DoagSpeakersMapper(input, Type type = Type.DEFAULT) {
        addSpeakers(input, type)
    }

    DoagSpeakersMapper addSpeakers(input, Type type = Type.DEFAULT) {
        this.photos.addAll input.findAll { it.PROFILFOTO }.PROFILFOTO
        this.speakers.putAll(fromSpeakerJson(input, type))
        return this
    }

    Set<String> getEventIds() {
        return eventIds2SpeakerIds.keySet()
    }

    private Map<String, Speaker> fromSpeakerJson(input, Type type) {
        input.collectEntries {row ->
            def speaker = new DoagSingleSpeakerMapper(row, type).speaker
            if (isEventData(row) && speaker) {
                speakerIds2EventsIds.put(speaker.id, row.ID.toString())
                eventIds2SpeakerIds.put(row.ID.toString(), speaker.id)
            }
            [(speaker?.id): speaker]
        }.findAll { k, v -> k && (type != Type.DEFAULT || speakers.keySet().contains(k)) }
    }

    private boolean isEventData(data) {
        return data.ID
    }
}
