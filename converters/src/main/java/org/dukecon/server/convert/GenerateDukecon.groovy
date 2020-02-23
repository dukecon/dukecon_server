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
import org.dukecon.server.convert.impl.SpeakerImageServiceFileExporter
import org.dukecon.server.convert.impl.DoagSpeakerImageService
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
            log.error("Error calling '{}': {} {}", GenerateDukecon.class, msg)
        }
        log.info("Usage: {} <conferenceUrl> <resourcesFolder>", GenerateDukecon.class)
        log.info("Example: {} file:conferences.yml .${File.separator}resources", GenerateDukecon.class)
        if (exitCode) {
            System.exit(exitCode)
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            usage("Wrong number of arguments", 1)
        }

        String inputUrlConferenceConfigFile = args[0]
        String inputPathForImageResourcesJson = "${args[1]}${File.separator}public${File.separator}img"
        String inputTemplateFileForStyleCss = "${args[1]}${File.separator}templates${File.separator}styles.gtl"

        ConferencesConfiguration conferencesConfiguration = ConferencesConfiguration.fromFile(inputUrlConferenceConfigFile, [:], false)
        conferencesConfiguration.conferences.each { ConferencesConfiguration.Conference conferenceConfig ->

            String outputConferenceStartDirectoryName = "htdocs${File.separator}rest${File.separator}${conferenceConfig.conference}${File.separator}${conferenceConfig.year}"

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
            SpeakerImageService speakerImageServiceImpl = new DoagSpeakerImageService()
            ConferenceDataExtractor conferenceDataExtractor =
                    conferenceDataExtractorClass.getConstructor(ConferencesConfiguration.Conference.class,
                            RawDataMapper.class, SpeakerImageService.class)
                            .newInstance(conferenceConfig, rawDataMapper, speakerImageServiceImpl)

            Conference conference = conferenceDataExtractor.conference

            String outputFileConferenceJson = "${outputConferenceStartDirectoryName}${File.separator}rest${File.separator}conferences${File.separator}${conferenceConfig.id}.json"
            ObjectMapper objectMapper = new ObjectMapper()
            File conferenceJson = new File(outputFileConferenceJson)
            conferenceJson.getParentFile().mkdirs()
            objectMapper.writeValue(conferenceJson, conference)
            log.info("Created {}", conferenceJson.absolutePath)

            File initJson = new File("${outputConferenceStartDirectoryName}${File.separator}rest${File.separator}init.json")
            objectMapper.writeValue(initJson, getInitJsonContent(conferenceConfig))
            log.info("Created {}", initJson.absolutePath)

            File imageResourcesJson = new File("${outputConferenceStartDirectoryName}${File.separator}rest${File.separator}image-resources.json")
            def imageResourcesJsonContent = getImageResourcesJsonContent(inputPathForImageResourcesJson, conferenceConfig.id)
            objectMapper.writeValue(imageResourcesJson, imageResourcesJsonContent)
            log.info("Created {}", imageResourcesJson.absolutePath)

            String outputPathSpeakerImages = "${outputConferenceStartDirectoryName}${File.separator}rest${File.separator}speaker${File.separator}images"
            def speakerImageExporter = new SpeakerImageServiceFileExporter(speakerImageServiceImpl, outputPathSpeakerImages)
            String outputDir = speakerImageExporter.export()
            log.info("Created {} with {} images", outputDir, speakerImageServiceImpl.images.size())

            def cssStylesOld = generateStylesCssContent(conferenceConfig, inputTemplateFileForStyleCss)
            File cssStylesFileOld = new File("${outputConferenceStartDirectoryName}${File.separator}rest${File.separator}styles.css")
            cssStylesFileOld.write(cssStylesOld)
            log.info("Created DEPRECATED {}", cssStylesFileOld.absolutePath)

            def cssStyles = generateStylesCssContent(conferenceConfig, inputTemplateFileForStyleCss)
            File cssStylesFile = new File("${outputConferenceStartDirectoryName}${File.separator}styles.css")
            cssStylesFile.write(cssStyles)
            log.info("Created {}", cssStylesFile.absolutePath)

            def faviconBytes = imageResourcesJsonContent.getConferenceFavIcon()
            if(faviconBytes) {
                File faviconFolder = new File("${outputConferenceStartDirectoryName}${File.separator}img")
                File faviconFile = new File("${faviconFolder.absolutePath}${File.separator}favicon.ico")
                faviconFolder.mkdirs()
                faviconFile.append(faviconBytes)
                log.info("Created {}", faviconFile.absolutePath)
            }
        }
    }

    private static String generateStylesCssContent(ConferencesConfiguration.Conference conference, String templateFileString) {
        Styles styles = new Styles(conference.getStyles())
        def templateEngine = new SimpleTemplateEngine()

        def templateFile = new File(templateFileString)

        if (templateFile.exists()) {
            def template = templateEngine.createTemplate(templateFile)
            return template.make([styles: styles])
        } else {
            log.error("${templateFileString} does not exist, styles.css will be empty!")
            return ""
        }
    }

    private static CoreImages getImageResourcesJsonContent(String imagePath, String conferenceId) {
        def images = CoreImages.builder().build()
        def resourceDir = "${imagePath}${File.separator}${conferenceId}"

        if(!new File(resourceDir).exists()) {
            log.warn("resource {} for image-resources.json does not exist", resourceDir)
        }

        setFilesAsByteArray("${resourceDir}${File.separator}conference", images.&setConferenceImage, "logo")
        setFilesAsByteArray("${resourceDir}${File.separator}favicon", images.&setConferenceFavIcon, "favicon")

        setFilesAsByteArray("${resourceDir}${File.separator}locations", images.&setLocationImages)
        setFilesAsByteArray("${resourceDir}${File.separator}location-maps", images.&setLocationMapImages)
        setFilesAsByteArray("${resourceDir}${File.separator}languages", images.&setLanguageImages)
        setFilesAsByteArray("${resourceDir}${File.separator}streams", images.&setStreamImages)

        images
    }

    private static void setFilesAsByteArray(String folder, Closure c, String nameOfSingleFile = null) {
        new ResourcesFinder(folder).fileList.ifPresent { files ->
            if (nameOfSingleFile) {
                c.call(Files.readAllBytes(files.get(nameOfSingleFile)?.toPath()))
            } else {
                c.call((Map<String, byte[]>)
                        files.collectEntries {
                            k, v -> [k, Files.readAllBytes(v.toPath())]
                        }
                )
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
