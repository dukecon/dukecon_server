package org.dukecon.server.herbstcampus

import com.xlson.groovycsv.CsvIterator
import org.dukecon.model.Language

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class HerbstcampusLanguageMapper {

    final List<Language> languages

    HerbstcampusLanguageMapper(input) {
        this.languages = [Language.builder().id("1").code('de').order(1).names([de: 'Deutsch', en: 'German']).icon("language_de.png").build()]
    }

    Language getDefaultLanguage() {
        return this.languages.first()
    }
}
