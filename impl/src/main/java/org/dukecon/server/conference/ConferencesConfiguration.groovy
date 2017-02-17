package org.dukecon.server.conference

import org.apache.commons.lang3.text.StrSubstitutor
import org.dukecon.adapter.ResourceWrapper

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.yaml.snakeyaml.Yaml

import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.time.LocalDate

@ConfigurationProperties(locations = "classpath:conferences.yml")
@Configuration
class ConferencesConfiguration {

    static ConferencesConfiguration fromFile(String classpathName, Map<String, Object> allProperties) {
        new ConferencesConfiguration(conferences: new Yaml(new YamlDateTimeConstructor()).load(new org.springframework.core.io.ClassPathResource(classpathName).inputStream).collect {
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
        LocalDate startDate

        LocalDate endDate

        @NotNull
        Object talksUri


        String talksUriEncoding = 'ISO-8859-1'

        String backupUri

        @NotNull
        Class extractorClass

        @Deprecated
        Class rawDataResourcesClass = ResourceWrapper.class

        Class rawDataMapperClass

        static Conference of(String id, String name, String url, String homeUrl) {
            new Conference(id: id, name: name, url: url, homeUrl: homeUrl)
        }

        @Override
        String toString() {
            return "$id: $name"
        }
    }

}
