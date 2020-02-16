package org.dukecon.server.convert

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.dukecon.model.Conference
import org.dukecon.model.CoreImages
import org.dukecon.model.Styles
import org.dukecon.server.conference.ConferencesConfiguration
import org.dukecon.server.conference.SpeakerImageService
import org.dukecon.server.repositories.ConferenceDataExtractor
import org.dukecon.server.repositories.RawDataMapper
import org.dukecon.server.repositories.RawDataResources
import org.dukecon.server.util.ResourcesFinder

import java.nio.file.Files
import java.time.format.DateTimeFormatter

@CompileStatic
@Slf4j
class GenerateDukecon {

    private static void usage(String msg = null, int exitCode = 0) {
        if (msg) {
            log.error("Error calling '{}': {}", GenerateDukecon.class, msg)
        }
        log.info("Usage: {} <conferenceUrl>", GenerateDukecon.class)
        if (exitCode) {
            System.exit(exitCode)
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage("Wrong number of arguments", 1)
        }
        ConferencesConfiguration conferencesConfiguration = ConferencesConfiguration.fromFile(args[0], [:], false)
        conferencesConfiguration.conferences.each { ConferencesConfiguration.Conference conferenceConfig ->
            RawDataResources rawDataResources = RawDataResources.of(conferenceConfig)
            Class rawDataMapperClass = conferenceConfig.rawDataMapperClass as Class
            RawDataMapper rawDataMapper =
                    rawDataMapperClass.getConstructor(RawDataResources.class)
                            .newInstance(rawDataResources)
            rawDataMapper.initMapper()
            if (rawDataMapper.isEmpty()) {
                log.error("Could not read input data")
                System.exit(-1)
            }
            Class conferenceDataExtractorClass = conferenceConfig.extractorClass as Class
            ConferenceDataExtractor conferenceDataExtractor =
                    conferenceDataExtractorClass.getConstructor(ConferencesConfiguration.Conference.class,
                            RawDataMapper.class, SpeakerImageService.class)
                            .newInstance(conferenceConfig, rawDataMapper, new DoagSpeakerImageService('images'))
            Conference conference = conferenceDataExtractor.conference
            ObjectMapper objectMapper = new ObjectMapper()
            String conferenceStartDirectoryName = "htdocs/rest/${conferenceConfig.conference}/${conferenceConfig.year}/rest"
            File conferenceJson = new File("${conferenceStartDirectoryName}/conferences/${conferenceConfig.id}.json")
            conferenceJson.getParentFile().mkdirs()
            objectMapper.writeValue(conferenceJson, conference)
            log.info("Created {}", conferenceJson.absolutePath)

            File initJson = new File("${conferenceStartDirectoryName}/init.json")
            objectMapper.writeValue(initJson, getInitJsonContent(conferenceConfig))
            log.info("Created {}", initJson.absolutePath)

            File imageResourcesJson = new File("${conferenceStartDirectoryName}/image-resources.json")
            objectMapper.writeValue(imageResourcesJson, getImageResourcesJsonContent(conferenceConfig))
            log.info("Created {}", imageResourcesJson.absolutePath)

            def cssStyles = generateStylesCssContent(conferenceConfig)
            File stylesCss = new File("${conferenceStartDirectoryName}/styles.css")
            stylesCss.write(cssStyles)
            log.info("Created {}", stylesCss.absolutePath)
        }
    }

    private static String generateStylesCssContent(ConferencesConfiguration.Conference conference) {
        Styles styles = new Styles(conference.getStyles())
        def templateEngine = new SimpleTemplateEngine()
        def template = templateEngine.createTemplate(this.class.getResource('/templates/styles.gtl').newReader());
        return template.make([styles: styles])
    }

    private static CoreImages getImageResourcesJsonContent(ConferencesConfiguration.Conference c) {
        def images = CoreImages.builder().build()
        def resourceDir = "img/${c.id}"

        setFilesAsByteArray("${resourceDir}/conference", images.&setConferenceImage, "logo")
        setFilesAsByteArray("${resourceDir}/favicon", images.&setConferenceFavIcon, "favicon")

        setFilesAsByteArray("${resourceDir}/locations", images.&setLocationImages)
        setFilesAsByteArray("${resourceDir}/location-maps", images.&setLocationMapImages)
        setFilesAsByteArray("${resourceDir}/languages", images.&setLanguageImages)
        setFilesAsByteArray("${resourceDir}/streams", images.&setStreamImages)

        images
    }

    private static void setFilesAsByteArray(String folder, Closure c, String nameOfSingleFile = null) {
        new ResourcesFinder(folder).fileList.ifPresent { files ->
            if (nameOfSingleFile) {
                c.call(Files.readAllBytes(files.get(nameOfSingleFile)?.toPath()))
            } else {
                c.call((Map<String, byte[]>) files.collectEntries { k, v -> [k, Files.readAllBytes(v.toPath())] })
            }
        }
    }

    private static Map<String, Object> getInitJsonContent(ConferencesConfiguration.Conference c) {
        final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE
        [
                id             : c.id,
                name           : c.name,
                year           : c.year,
                url            : c.url,
                homeUrl        : c.homeUrl,
                homeTitle      : c.homeTitle,
                imprint        : [
                        de: c.imprint.de,
                        en: c.imprint.en
                ],
                termsOfUse     : c.termsOfUse,
                privacy        : c.privacy,
                startDate      : dtf.format(c.startDate),
                endDate        : dtf.format(c.endDate),
                authEnabled    : c.authEnabled,
                admin          : "../rest/admin/${c.id}".toString(),
                forceUpdate    : "../rest/conferences/update/${c.id}".toString(),
                conferences    : "../rest/conferences/${c.id}".toString(),
                events         : "../rest/eventsBooking/${c.id}".toString(),
                keycloak       : "../rest/keycloak.json",
                favoritesExport: "../rest/favorites/${c.id}".toString(),
                feedbackServer : [
                        active         : Boolean.valueOf(c.feedbackServer.active),
                        timeSlotVisible: c.feedbackServer.timeSlotVisible as int
                ]
        ]
    }
}
