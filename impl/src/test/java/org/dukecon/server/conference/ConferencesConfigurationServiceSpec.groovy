package org.dukecon.server.conference

import javax.inject.Inject

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ConferencesConfigurationServiceSpec extends AbstractDukeConSpec {
    @Inject
    private ConferencesConfigurationService configurationService

    void "get javaland 2017 configuration"() {
        when:
        def jl2017 = configurationService.getConference('javaland', '2017')
        then:
        jl2017.id == 'jl2017'
        jl2017.name == 'JavaLand 2017'
        jl2017.url.startsWith('http://')
        jl2017.url.endsWith('/2017')
        jl2017.year == '2017'
    }

    void "return null if conference does not exist"() {
        when:
        def jl2010 = configurationService.getConference('javaland', '2010')
        then:
        !jl2010

        when:
        def foobar2010 = configurationService.getConference('foobar', '2010')
        then:
        !foobar2010
    }

}
