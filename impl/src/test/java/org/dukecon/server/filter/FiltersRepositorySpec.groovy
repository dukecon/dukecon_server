package org.dukecon.server.filter

import groovy.util.logging.Slf4j
import org.dukecon.server.conference.AbstractDukeConSpec
import org.springframework.dao.DataIntegrityViolationException

import javax.inject.Inject

/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */
@Slf4j
class FiltersRepositorySpec extends AbstractDukeConSpec {
    @Inject
    FiltersRepository filtersRepository

    void "test simple insert"() {
        when:
        Filters savedFilters = filtersRepository.save(new Filters(principalId: "4711", favourites: true, languages: ["Englisch"]))
        then:
        assert null != savedFilters
        log.debug("New Filters has id {} and languages {}", savedFilters.id, savedFilters.languages)
    }

    void "test insert and retrieve"() {
        when:
        def all = filtersRepository.findAll()
        Filters savedFilters = filtersRepository.save(new Filters(principalId: "4712", favourites: true, languages: ["Englisch"]))
        all = filtersRepository.findAll()
        Filters result = filtersRepository.findByPrincipalId("4712")
        then:
        assert null != savedFilters
        log.debug("New Filters has id {} and languages {}", savedFilters.id, savedFilters.languages)
        assert null != result
    }

    void "test constraint violation"() {
        when:
        filtersRepository.save(new Filters(principalId: "4711", favourites: true, languages: ["Englisch"]))
        filtersRepository.save(new Filters(principalId: "4711", favourites: true, languages: ["Deutsch"]))
        then:
        DataIntegrityViolationException e = thrown(DataIntegrityViolationException)
        log.debug("Expected exception '{}' was thrown", e.message)
    }
}
