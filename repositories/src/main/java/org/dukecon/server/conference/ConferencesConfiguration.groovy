package org.dukecon.server.conference

import groovy.util.logging.Slf4j
import org.apache.commons.lang3.text.StrSubstitutor
import org.dukecon.adapter.ResourceWrapper
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import org.yaml.snakeyaml.Yaml

import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.time.LocalDate

@ConfigurationProperties
@Configuration
@Slf4j
@Validated
class ConferencesConfiguration {

    static ConferencesConfiguration fromFile(String classpathName, Map<String, Object> allProperties) {
        log.debug ("Loading configuration data from '{}'", classpathName)
        new ConferencesConfiguration(conferences:
                new Yaml(new org.dukecon.server.conference.YamlDateTimeConstructor()).load(
                    ResourceWrapper.of(classpathName).stream
                ).collect {
                    new Conference(substitutePlaceHolder(it, allProperties))
                })
    }

    private static Map substitutePlaceHolder(Map properties, Map allConfigProperties) {
        def substitutor = new StrSubstitutor(properties + allConfigProperties)
        properties.each {k, v ->
            if (v instanceof String) {
                properties[k] = substitutor.replace(v)
            }
            if (v instanceof Map) {
                def substitutor2 = new StrSubstitutor(properties + allConfigProperties)
                v.each {k1, v1 ->
                    v[k1] = substitutor.replace(v1)
                }
            }
        }
        properties
    }

    @NotNull
    @Valid
    List<Conference> conferences = [];

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
		
		@NotNull
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
            dark: '#1aa3b1',
            darkLink: '#1aa3b1',
            hover: '#00c3d7',
            hoverLink: '#00c3d7',
            reverse: '#fff',
            highlight: '#ddee55',
            alternate: '#bf5a00',
        ]

        Class rawDataMapperClass

        Map<String, String> feedbackServer = [
                timeSlotVisible: -1,
                active: true
        ]

        static Conference of(String id, String name, String url, String homeUrl) {
            new Conference(id: id, name: name, url: url, homeUrl: homeUrl)
        }

        @Override
        String toString() {
            return "$id: $name"
        }
    }

}
