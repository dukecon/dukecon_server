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
		jl2017.imprint.de == "https://www.javaland.eu/de/impressum/"
		jl2017.imprint.en == "https://www.javaland.eu/en/imprint/"
		jl2017.termsOfUse.de == "https://www.javaland.eu/de/nutzungsbedingungen/"
		jl2017.termsOfUse.en == "https://www.javaland.eu/en/term-of-use/"
		jl2017.privacy.de == "https://www.javaland.eu/de/datenschutz/"
		jl2017.privacy.en == "https://www.javaland.eu/en/privacy/"
        jl2017.year == '2017'
		jl2017.authEnabled == true
    }
	
	void "get jfs 2016 configuration"() {
		when:
		def jfs2016 = configurationService.getConference('jfs', '2016')
		then:
		jfs2016.id == 'jfs2016'
		jfs2016.name == 'Java Forum Stuttgart 2016'
		jfs2016.url == 'https://jfs.dukecon.org/2016'
		jfs2016.imprint.de == "https://www.java-forum-stuttgart.de/de/Impressum.html"
		jfs2016.imprint.en == "https://www.java-forum-stuttgart.de/de/Impressum.html"
		jfs2016.termsOfUse == null
		jfs2016.privacy == null
		jfs2016.year == '2016'
		jfs2016.authEnabled == false
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
