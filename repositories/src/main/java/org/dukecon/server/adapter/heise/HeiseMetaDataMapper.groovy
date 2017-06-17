package org.dukecon.server.adapter.heise

import org.dukecon.model.MetaData

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HeiseMetaDataMapper {

    final MetaData metaData

    HeiseMetaDataMapper(HeiseStreamMapper streamMapper, HeiseLocationMapper locationMapper, HeiseLanguageMapper languageMapper, HeiseAudienceMapper audienceMapper, HeiseEventTypeMapper eventTypeMapper) {
        this.metaData = MetaData.builder()
                .tracks(streamMapper.entities)
                .locations(locationMapper.entities)
                .languages(languageMapper.languages)
                .defaultLanguage(languageMapper.defaultLanguage)
                .audiences(audienceMapper.entities)
                .eventTypes(eventTypeMapper.entities)
                .defaultIcon("Unknown.png")
                .build()

    }
}
