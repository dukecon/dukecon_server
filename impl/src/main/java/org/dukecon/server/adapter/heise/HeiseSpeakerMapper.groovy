package org.dukecon.server.adapter.heise

import com.google.common.collect.HashMultimap
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Multimap
import com.xlson.groovycsv.CsvIterator
import org.dukecon.model.Speaker

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseSpeakerMapper {

    private final Map<String, Speaker> speakers = Maps.newHashMap()
    private final Multimap<String, Speaker> eventIdsToSpeaker = HashMultimap.create()

    HeiseSpeakerMapper(input) {
        input.each {
            String eventId = it.values[0]
            addSpeaker(it.Vorname1, it.Nachname1, it.Email1, it.Twitter1, it.Firma1, it.Autor1, eventId)
            addSpeaker(it.Vorname2, it.Nachname2, it.Email2, it.Twitter2, it.Firma2, it.Autor2, eventId)
            addSpeaker(it.Vorname3, it.Nachname3, it.Email3, null, it.Firma3, it.Autor3, eventId)
        }
    }

    private void addSpeaker(String firstname, String lastname, String email, String twitter, String company, String bio, String eventId) {
        if (lastname && email) {
            Speaker speaker = Speaker.builder()
                    .id(Integer.toString("${firstname}_${lastname}_${email}".hashCode()))
                    .name("${firstname} ${lastname}")
                    .email("${email}")
                    .company(company)
                    .twitter(twitter)
                    .bio(bio)
                    .events([])
                    .build()
            if (speakers.containsKey(speaker.id)) {
                eventIdsToSpeaker.put(eventId, speakers.get(speaker.id))
            } else {
                speakers.put(speaker.id, speaker)
                eventIdsToSpeaker.put(eventId, speaker)
            }
        }
    }

    List<Speaker> getSpeakers() {
        speakers.values().toList()
    }

    List<Speaker> getSpeakersForEvent(String eventId) {
        new ArrayList<>(eventIdsToSpeaker.get(eventId))
    }
}
