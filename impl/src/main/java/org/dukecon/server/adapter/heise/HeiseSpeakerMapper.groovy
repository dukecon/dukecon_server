package org.dukecon.server.adapter.heise

import com.google.common.collect.HashMultimap
import com.google.common.collect.Maps
import com.google.common.collect.Multimap
import org.dukecon.adapter.ResourceWrapper
import org.dukecon.model.Speaker
import org.dukecon.server.speaker.SpeakerImageService

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseSpeakerMapper {

    private final Map<String, Speaker> speakers = Maps.newHashMap()
    private final Multimap<String, Speaker> eventIdsToSpeaker = HashMultimap.create()
    private final SpeakerImageService speakerImageService

    HeiseSpeakerMapper(input, SpeakerImageService speakerImageService) {
        this.speakerImageService = speakerImageService
        input.each {
            String eventId = it.values[0]
            addSpeaker(it.Vorname1, it.Nachname1, it.Email1, it.Twitter1, it.Firma1, it.Autor1, eventId, 1)
            addSpeaker(it.Vorname2, it.Nachname2, it.Email2, it.Twitter2, it.Firma2, it.Autor2, eventId, 2)
            addSpeaker(it.Vorname3, it.Nachname3, it.Email3, null, it.Firma3, it.Autor3, eventId, 3)
        }
    }

    private void addSpeaker(String firstname, String lastname, String email, String twitter, String company, String bio, String eventId, int lfdNo = 1) {
        if (lastname && email) {
            byte[] photo = ResourceWrapper.of("herbstcampus-2016/images/${eventId}_${lfdNo}.jpg").stream?.bytes
            def photoId = photo ? speakerImageService.addImage(photo, "${eventId}_${lfdNo}.jpg") : null
            Speaker speaker = Speaker.builder()
                    .id(Integer.toString("${firstname}_${lastname}_${email}".hashCode()))
                    .name("${firstname} ${lastname}")
                    .firstname(firstname)
                    .lastname(lastname)
                    .email("${email}")
                    .company(company)
                    .twitter(twitter)
                    .bio(bio)
                    .photoId(photoId)
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
