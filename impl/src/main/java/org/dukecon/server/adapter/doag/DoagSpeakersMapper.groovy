package org.dukecon.server.adapter.doag

import org.dukecon.model.Speaker

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class DoagSpeakersMapper {

    final Map<String, Speaker> speakers

    DoagSpeakersMapper(input) {
        this.speakers = input.collectEntries {
            def speaker = new DoagSingleSpeakerMapper(it).speaker
            [(speaker.id): speaker]
        }
    }
}
