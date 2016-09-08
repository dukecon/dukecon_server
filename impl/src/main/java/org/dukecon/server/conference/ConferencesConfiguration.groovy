package org.dukecon.server.conference

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.format.annotation.DateTimeFormat

import javax.validation.Valid
import javax.validation.constraints.NotNull

@ConfigurationProperties(locations = "classpath:conferences.yml")
@Configuration
class ConferencesConfiguration {

    @NotNull
    @Valid
    List<ConferenceConfiguration> conferences = [];

    public static class ConferenceConfiguration {
        @NotNull
        String id

        @NotNull
        String conferenceName

        @NotNull
        String conferenceUrl

        @NotNull
        String startDate

        String endDate

        @NotNull
        String talksUri

        String backupUri

        @Override
        String toString() {
            return "$id: $conferenceName"
        }
    }

}
