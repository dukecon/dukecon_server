package org.dukecon.server.gui;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.dukecon.server.util.arquillian.ContextRule;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.ArquillianClassRule;
import org.jboss.arquillian.junit.ArquillianRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;

import java.net.URL;

/**
 * Base class for all Selenium/Graphene webpage tests.
 */
public class AbstractPageTest<GIVEN, WHEN, THEN> extends ScenarioTest<GIVEN, WHEN, THEN> {

    @ClassRule
    public static ArquillianClassRule classRule = new ArquillianClassRule();

    @Value("${local.server.port}")
    int port;

    /**
     * Althogh this is not used within this class, it is needed to trigger the
     * Arquillian's Graphene initialization.
     */
    @Drone
    public WebDriver browser;

    @Autowired
    private EmbeddedWebApplicationContext server;

    /**
     * Inject the URL to Arquillian so @Location annotations work as expected.
     * Needs to be placed before the ArquillianRule.
     */
    @Rule
    public ContextRule contextRule = new ContextRule(
            () -> new URL("http://localhost:"
            + port + server.getServletContext().getContextPath() + "/"));

    @Rule
    public ArquillianRule arquillianRule = new ArquillianRule();

}
