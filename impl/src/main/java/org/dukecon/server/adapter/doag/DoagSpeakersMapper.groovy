package org.dukecon.server.adapter.doag

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import org.dukecon.model.Speaker
import org.dukecon.server.adapter.doag.DoagSingleSpeakerMapper.Type
import org.dukecon.server.speaker.SpeakerImageResource

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagSpeakersMapper {

    final Map<String, Speaker> speakers = [:]
    final List photos = []
    final Multimap<String, String> eventIds2SpeakerIds = HashMultimap.create()
    final Multimap<String, String> speakerIds2EventIds = HashMultimap.create()

    static DoagSpeakersMapper createFrom(eventInput, speakerInput, twitterHandles = [:]) {
        new DoagSpeakersMapper(eventInput, DoagSingleSpeakerMapper.Type.REFERENT)
                .addSpeakers(eventInput, DoagSingleSpeakerMapper.Type.COREFERENT)
                .addSpeakers(eventInput, DoagSingleSpeakerMapper.Type.COCOREFERENT)
                .addSpeakers(speakerInput)
                .mergeAdditionalTwitterHandles(twitterHandles)
    }

    DoagSpeakersMapper(input, Type type = Type.DEFAULT) {
        addSpeakers(input, type)
    }

    DoagSpeakersMapper addSpeakers(input, Type type = Type.DEFAULT) {
        this.photos.addAll input.findAll { it.PROFILFOTO }*.PROFILFOTO
        this.speakers.putAll(fromSpeakerJson(input, type).findAll { k, v -> k && (type != Type.DEFAULT || speakers.keySet().contains(k) || eventIds2SpeakerIds.isEmpty()) })
        return this
    }

    DoagSpeakersMapper mergeAdditionalTwitterHandles(Map<String, String> twitterHandles) {
        speakers.values().each {Speaker s ->
            if (!s.twitter && twitterHandles.containsKey(s.name)) {
                s.twitter = twitterHandles.get(s.name)
            }
        }
        return this
    }

    Set<String> getEventIds() {
        return eventIds2SpeakerIds.keySet()
    }

    Speaker forEventId(String eventId) {
        return speakers.get(eventIds2SpeakerIds.get(eventId)?.first())
    }

    private Map<String, Speaker> fromSpeakerJson(input, Type type) {
        input?.collectEntries { row ->
            def speaker = new DoagSingleSpeakerMapper(row, type).speaker
            if (isEventData(row) && speaker) {
                speakerIds2EventIds.put(speaker.id, row.ID.toString())
                eventIds2SpeakerIds.put(row.ID.toString(), speaker.id)
            }
            [(speaker?.id): speaker]
        }
    }

    private boolean isEventData(data) {
        return data.ID
    }
}
