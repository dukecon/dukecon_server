package org.dukecon.server.conference

import spock.lang.Ignore

import javax.inject.Inject

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
class ConferencesConfigurationServiceSpec extends AbstractDukeConSpec {
    @Inject
    private ConferencesConfigurationService configurationService

    // FIXME We need to skip this test, since the 'conferences-dev.yml' is read during initialization though
    // for integration tests only the 'conferences-integration.yml' is configured! In fact the later is read at first
    // but then the dev version is also read and overrides the conference configurations from the integration version.
    @Ignore
    void "get javaland 2017 configuration"() {
        when:
        def javaland2017 = configurationService.getConference('javaland', '2017')
        then:
        javaland2017.id == 'javaland2017'
        javaland2017.name == 'JavaLand 2017'
        javaland2017.url.startsWith('http://')
        javaland2017.url.endsWith('/2017')
		javaland2017.imprint.de == "https://www.javaland.eu/de/impressum/"
		javaland2017.imprint.en == "https://www.javaland.eu/en/imprint/"
		javaland2017.termsOfUse.de == "https://www.javaland.eu/de/nutzungsbedingungen/"
		javaland2017.termsOfUse.en == "https://www.javaland.eu/en/term-of-use/"
		javaland2017.privacy.de == "https://www.javaland.eu/de/datenschutz/"
		javaland2017.privacy.en == "https://www.javaland.eu/en/privacy/"
        javaland2017.year == '2017'
		javaland2017.authEnabled == true
    }
	
    void "return null if conference does not exist"() {
        when:
        def javaland2010 = configurationService.getConference('javaland', '2010')
        then:
        !javaland2010

        when:
        def foobar2010 = configurationService.getConference('foobar', '2010')
        then:
        !foobar2010
    }

}
