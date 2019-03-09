package org.dukecon.server.repositories.doag

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import groovy.util.logging.Slf4j
import org.dukecon.model.Speaker
import org.dukecon.server.repositories.doag.DoagSingleSpeakerMapper.Type

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@Slf4j
class DoagSpeakersMapper {

    final Map<String, Speaker> speakers = [:]
    final List photos = []
    final Multimap<String, String> eventIds2SpeakerIds = HashMultimap.create()
    final Multimap<String, String> speakerIds2EventIds = HashMultimap.create()

    static DoagSpeakersMapper createFrom(eventInput, speakerInput, twitterHandles = [:]) {
        log.debug("Create speakers from #{} events, #{} speakerextensions and #{} twitterhandles",
            eventInput?.size(), speakerInput?.size(), twitterHandles?.size)
        DoagSpeakersMapper result = new DoagSpeakersMapper(eventInput, DoagSingleSpeakerMapper.Type.REFERENT)
                .addSpeakers(eventInput, DoagSingleSpeakerMapper.Type.COREFERENT)
                .addSpeakers(eventInput, DoagSingleSpeakerMapper.Type.COCOREFERENT)
                .mergeAdditionalSpeakerInfos(speakerInput)
                .mergeAdditionalTwitterHandles(twitterHandles)
        log.debug("Finally found #{} speakers", result?.speakers?.size())
        return result
    }

    DoagSpeakersMapper(input, Type type = Type.DEFAULT) {
        addSpeakers(input, type)
    }

    DoagSpeakersMapper addSpeakers(input, Type type) {
        this.photos.addAll input.findAll { it.PROFILFOTO }*.PROFILFOTO
        this.speakers.putAll(fromSpeakerJson(input, type).findAll {
            k, v -> k && (type != Type.DEFAULT || speakers.keySet().contains(k) || eventIds2SpeakerIds.isEmpty())
        })
        return this
    }

    DoagSpeakersMapper mergeAdditionalSpeakerInfos(speakerInput) {
        Map<String, Speaker> additionalSpeakerInput = fromSpeakerJson(speakerInput, Type.DEFAULT)
        this.photos.addAll speakerInput.findAll { it.PROFILFOTO }*.PROFILFOTO
        if (!additionalSpeakerInput) {
            log.warn ("Additional speaker data is empty")
            return this
        }
        speakers.keySet().each {String key ->
            if (additionalSpeakerInput[key]) {
                if (!speakers[key].name) { speakers[key].name = additionalSpeakerInput[key]?.name}
                if (!speakers[key].firstname) { speakers[key].firstname = additionalSpeakerInput[key]?.firstname}
                if (!speakers[key].lastname) { speakers[key].lastname = additionalSpeakerInput[key]?.lastname}
                if (!speakers[key].company) { speakers[key].company = additionalSpeakerInput[key]?.company}
                if (!speakers[key].email) { speakers[key].email = additionalSpeakerInput[key]?.email}
                if (!speakers[key].website) { speakers[key].website = additionalSpeakerInput[key]?.website}
                if (!speakers[key].gplus) { speakers[key].gplus = additionalSpeakerInput[key]?.gplus}
                if (!speakers[key].facebook) { speakers[key].facebook = additionalSpeakerInput[key]?.facebook}
                if (!speakers[key].xing) { speakers[key].xing = additionalSpeakerInput[key]?.xing}
                if (!speakers[key].linkedin) { speakers[key].linkedin = additionalSpeakerInput[key]?.linkedin}
                if (!speakers[key].twitter) { speakers[key].twitter = additionalSpeakerInput[key]?.twitter}
                if (!speakers[key].bio) { speakers[key].bio = additionalSpeakerInput[key]?.bio}
                if (!speakers[key].photoId) { speakers[key].photoId = additionalSpeakerInput[key]?.photoId }
            }
        }
//        additionalSpeakerInput.keySet().each {String key->
//            if (!speakers.containsKey(key)) {
//                log.debug ("Adding missing speaker '{}'", additionalSpeakerInput[key].name)
//                speakers[key] = additionalSpeakerInput[key]
//            }
//        }
        return this
    }

    DoagSpeakersMapper mergeAdditionalTwitterHandles(Map<String, String> twitterHandles) {
        speakers.values().each {Speaker s ->
            if (!s.twitter && twitterHandles.containsKey(s.name)) {
                s.twitter = twitterHandles.get(s.name)
                log.debug("Adding Twitter handle '{}' from local data for '{}'", s.twitter, s.name)
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
        log.debug("Converting #{} additional speakers", input?.size())
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
