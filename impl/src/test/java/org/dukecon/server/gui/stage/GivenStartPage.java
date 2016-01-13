package org.dukecon.server.gui.stage;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.dukecon.server.gui.page.StartPage;
import org.hamcrest.Matchers;
import org.jboss.arquillian.drone.api.annotation.Default;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.openqa.selenium.HasCapabilities;

import static org.junit.Assume.assumeThat;

public class GivenStartPage extends AbstractStage<GivenStartPage> {

    @ProvidedScenarioState
    private StartPage startPage;

    public GivenStartPage start_page_opened_in_browser() {
        startPage = Graphene.goTo(StartPage.class);
        startPage.verify();
        return this;
    }

    public GivenStartPage not_logged_in() {
        if (startPage.isLoggedIn()) {
            startPage.logoff();
        }
        return this;
    }

    public GivenStartPage you_use_firefox() {
        // the redirect to the login page doesn't work on PhantomJS
        // FIXME: remove once server side testing has been moved to Firefox
        HasCapabilities c = (HasCapabilities) GrapheneContext.getContextFor(Default.class).getWebDriver();
        assumeThat(c.getCapabilities().getBrowserName(), Matchers.equalToIgnoringCase("firefox"));

        return this;
    }
}
