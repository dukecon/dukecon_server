package org.dukecon.server.business

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonSlurper
import org.dukecon.model.Talk
import org.springframework.stereotype.Component

/**
 * @author Niko Köbler, http://www.n-k.de, @dasniko
 */
@Component
class TalkProvider {

    def List<Talk> getAllTalks() {
        readDemoFile() as List<Talk>
    }

    private def readDemoFile() {
        def mapper = new ObjectMapper()
        def is = this.getClass().getResourceAsStream('/demotalks.json')
        def slurper = new JsonSlurper()
        def json = slurper.parse(is)
        def talks = []
        json.each {
            talks.add(mapper.convertValue(it, Talk.class))
        }
    }

}
