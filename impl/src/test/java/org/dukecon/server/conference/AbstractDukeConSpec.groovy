package org.dukecon.server.conference

import org.dukecon.DukeConServerApplication
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = DukeConServerApplication, initializers = DukeConServerApplication.DataProviderInitializer)
@WebAppConfiguration
@IntegrationTest(["server.port=0"])
class AbstractDukeConSpec extends Specification {
}
