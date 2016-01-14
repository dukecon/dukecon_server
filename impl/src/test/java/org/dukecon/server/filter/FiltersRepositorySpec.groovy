package org.dukecon.server.filter

import groovy.util.logging.Slf4j
import org.dukecon.DukeConServerApplication
import org.dukecon.server.filter.Filters
import org.dukecon.server.filter.FiltersRepository
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import javax.inject.Inject

/**
 * @author Gerd Aschemann, http://aschemann.net, @GerdAschemann
 */
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = DukeConServerApplication)
@WebAppConfiguration
@IntegrationTest(["server.port=0"])
@Slf4j
class FiltersRepositorySpec extends Specification {
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
