@Grab('com.xlson.groovycsv:groovycsv:1.3')

import com.xlson.groovycsv.PropertyMapper
import groovy.json.JsonBuilder
import groovy.json.JsonOutput

/**
 * Created by ascheman on 28.05.17.
 */
import static com.xlson.groovycsv.CsvParser.parseCsv

FileReader csvFile = new FileReader('src/main/resources/jfs-2017-formes-dump.csv')
hits = []
for(PropertyMapper line in parseCsv(csvFile, separator: ',')) {
    println "Titel: ${line.TITEL}, Referent: ${line.REFERENT_NAME}"
    def hit = [
            _source:
                    line.toMap()
    ]
//    println (hit)
    hits.add(hit)
}

JsonBuilder jsonBuilder = new JsonBuilder({
    took "unknown"
    timed_out false
    _shards {
        "total" 5
        "successful" 5
        "failed" 0
    }
    "total" hits.size()
    "max_score" 1.0
    "hits" {
        hits hits
    }
})

File output = new File("src/main/resources/jfs-2017.raw.json")
output.withWriter () {writer ->
    writer.write(jsonBuilder.toPrettyString())
}