package org.dukecon.server.business

import lombok.AllArgsConstructor
import org.dukecon.model.Audience
import org.dukecon.model.MetaData
import org.dukecon.model.Room
import org.dukecon.model.TalkType
import org.dukecon.model.Track

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class MetaDataLookup {
    private MetaData metaData
    Map<String, Integer> rooms
    Map<String, Integer> audiences
    Map<String, Integer> tracks
    Map<String, Integer> talkTypes

    MetaDataLookup(MetaData metaData) {
        this.metaData = metaData
        initReverseLookup()
    }

    void initReverseLookup() {
        rooms = init(metaData.rooms, {Room r -> r.name})
        audiences = init(metaData.audiences, {Audience a -> a.names['de']})
        tracks = init(metaData.tracks, {Track t -> t.names['de']})
        talkTypes = init(metaData.talkTypes, {TalkType t -> t.names['de']})
    }

    private Map init(elements, Closure getName) {
        return elements.inject([:]) {map, element ->
            map[getName(element)] = element.order
            map
        }
    }
}
