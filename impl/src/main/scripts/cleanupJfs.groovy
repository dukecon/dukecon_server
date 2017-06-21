#!/usr/bin/env groovy

package org.dukecon.server

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * Created by ascheman on 28.05.17.
 */

JsonSlurper jsonSlurper = new JsonSlurper ()
File input = new File("src/main/resources/jfs-2016-final-finished-conf.raw.json")
String contents = input.getText("ISO-8859-1")
def json = jsonSlurper.parseText(contents)

def ids = [:]
def newhits = []
json.hits.hits.each { def hit ->
    def source = hit._source
    def id = source.ID
    if (ids[id]) {
//        println "Duplicate ${id}: ${source.REFERENT_NAME}"
    } else {
        newhits.add(hit)
        ids[id] = true
    }
}

JsonBuilder jsonBuilder = new JsonBuilder({
    took "unknown"
    timed_out false
    _shards {
        "total" 5
        "successful" 5
        "failed" 0
    }
    "total" newhits.size()
    "max_score" 1.0
    "hits" {
        hits newhits
    }
})

File output = new File("src/main/resources/jfs-2016-final-finished-conf-reduced.raw.json")
output.withWriter ("ISO-8859-1") {writer ->
    def prettyJson = JsonOutput.prettyPrint(jsonBuilder.toString())
    writer.write(prettyJson)
}
