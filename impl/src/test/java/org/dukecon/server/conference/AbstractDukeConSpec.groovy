package org.dukecon.server.conference

import org.dukecon.DukeConServerApplication
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@ContextConfiguration(loader = SpringBootContextLoader, classes = DukeConServerApplication, initializers = DukeConServerApplication.DataProviderInitializer)
@WebAppConfiguration
@ActiveProfiles("integrationtest")
@SpringBootTest (["server.port=0"])
class AbstractDukeConSpec extends Specification {
}
