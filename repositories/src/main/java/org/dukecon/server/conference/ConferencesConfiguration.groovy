package org.dukecon.server.conference

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.text.StrSubstitutor
import org.dukecon.adapter.ResourceWrapper
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import org.yaml.snakeyaml.Yaml

import javax.validation.*
import javax.validation.constraints.NotNull
import java.time.LocalDate

@Configuration
@Slf4j
@Validated
class ConferencesConfiguration {

    static ConferencesConfiguration fromFile(String classpathName, Map<String, Object> allProperties) {
        log.debug("Loading configuration data from '{}'", classpathName)
        new ConferencesConfiguration(conferences:
                readConferences(classpathName, allProperties))
    }

    private static List<Conference> readConferences(String classpathName, Map<String, Object> allProperties) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
        Validator validator = factory.getValidator()
        def yaml = readYaml(classpathName)
        if (yaml) {
            if (yaml instanceof List) {
                yaml = yaml.findAll{it}.collectEntries{[(it.id): it]}
            }
            def conferences = deepCopy(yaml).findAll { k, v -> k ==~ /^.*\d+$/ }.findResults { k, v ->
                def conferenceProperties = v << [id: k]
                def conference = new Conference(substitutePlaceHolder(conferenceProperties ?: [:], allProperties))
                Set<ConstraintViolation<Conference>> violations = validator.validate(conference)
                for (ConstraintViolation<Conference> violation : violations) {
                    log.error("{}.{} {}", violation.getRootBeanClass().getSimpleName(), violation.propertyPath, violation.getMessage())
                }
                violations?.isEmpty() ? conference : null
            }
            return conferences
        }
        return []
    }

    /**
     * Make a deep copy of each (sub) map because of referenced sub maps (copy by reference) between different conferences.
     * While substitution the placeholder in referenced sub maps would be replaced only once and further reference would
     * contain the wrong value.
     *
     * @param m to copy deeply
     * @return new map
     */
    private static Map deepCopy(Map m) {
        new HashMap(m).collectEntries {k, v -> [k, v instanceof Map ? deepCopy(v) : v]}
    }

    private static Object readYaml(String classpathName) {
        def data = ResourceWrapper.of(classpathName).stream;
        log.debug("Reading data stream from '{}'", data)
        try {
            return new Yaml(new YamlDateTimeConstructor())
                    .load(data)
        } catch (Exception e) {
            log.error("Could not read conference configuration yaml file: {}", e, classpathName)
            throw new IllegalStateException("Could not read conference configuration yaml file: " + classpathName, e)
        }
    }

    private static Map substitutePlaceHolder(Map properties, Map allConfigProperties) {
        def substitutor = new StrSubstitutor(properties + allConfigProperties)
        properties.each { k, v ->
            if (v instanceof String) {
                properties[k] = substitutor.replace(v)
            }
            if (v instanceof Map) {
                def substitutor2 = new StrSubstitutor(properties + allConfigProperties)
                v.each { k1, v1 ->
                    v[k1] = substitutor.replace(v1)
                }
            }
        }
        properties
    }

    @NotNull
    @Valid
    List<Conference> conferences = []

    static class Conference {
        @NotNull
        String id

        String internalId

        @NotNull
        String conference

        @NotNull
        String year

        @NotNull
        String name

        @NotNull
        String url

        String homeTitle

        @NotNull
        String homeUrl

        Map<String, String> imprint

        Map<String, String> termsOfUse

        Map<String, String> privacy

        @NotNull
        LocalDate startDate

        LocalDate endDate

        /*
         * Authentication switch for client.
         */
        @NotNull
        Boolean authEnabled = false

        @NotNull
        Object talksUri

        String talksUriEncoding = 'ISO-8859-1'

        String backupUri

        @NotNull
        Class extractorClass

        @Deprecated
        Class rawDataResourcesClass = ResourceWrapper.class

        @NotNull
        Map<String, String> styles = [
                dark     : '#1aa3b1',
                darkLink : '#1aa3b1',
                hover    : '#00c3d7',
                hoverLink: '#00c3d7',
                reverse  : '#fff',
                highlight: '#ddee55',
                alternate: '#bf5a00',
        ]

        Class rawDataMapperClass

        Map<String, String> feedbackServer = [
                timeSlotVisible: -1,
                active         : false
        ]

        static Conference of(String id, String name, String url, String homeUrl) {
            new Conference(id: id, name: name, url: url, homeUrl: homeUrl)
        }

        /**
         * @return true if talks URI is a remote URL and no local file
         */
        boolean isRemoteTalksUri() {
            talksUri.toString().contains("http")
        }

        @Override
        String toString() {
            return "$id: $name"
        }
    }

}
