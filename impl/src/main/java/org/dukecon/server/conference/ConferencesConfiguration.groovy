package org.dukecon.server.conference

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.yaml.snakeyaml.Yaml

import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.time.LocalDate

@ConfigurationProperties(locations = "classpath:conferences.yml")
@Configuration
class ConferencesConfiguration {

    public static ConferencesConfiguration fromFile(String classpathName) {
        new ConferencesConfiguration(conferences: new Yaml(new YamlDateTimeConstructor()).load(getClass().getResourceAsStream("/${classpathName}")).collect {
            new Conference(it)
        })
    }

    @NotNull
    @Valid
    List<Conference> conferences = [];

    public static class Conference {
        @NotNull
        String id

        String internalId

        @NotNull
        String conference

        @NotNull
        Integer year

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
        String talksUri

        String backupUri

        @NotNull
        String extractorClass

        @Override
        String toString() {
            return "$id: $name"
        }
    }

}
