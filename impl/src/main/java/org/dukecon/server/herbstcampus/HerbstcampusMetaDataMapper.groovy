package org.dukecon.server.herbstcampus

import org.dukecon.model.MetaData

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusMetaDataMapper {

    final MetaData metaData

    HerbstcampusMetaDataMapper(HerbstcampusStreamMapper streamMapper, HerbstcampusLocationMapper locationMapper, HerbstcampusLanguageMapper languageMapper, HerbstcampusAudienceMapper audienceMapper, HerbstcampusEventTypeMapper eventTypeMapper) {
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
