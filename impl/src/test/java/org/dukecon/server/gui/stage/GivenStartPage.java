package org.dukecon.server.gui.stage;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.dukecon.server.gui.page.StartPage;
import org.jboss.arquillian.graphene.Graphene;

public class GivenStartPage extends AbstractStage<GivenStartPage> {

    @ProvidedScenarioState
    private StartPage startPage;

    public GivenStartPage start_page_opened_in_browser() {
        try {
            startPage = Graphene.goTo(StartPage.class);
            startPage.verify();
            return this;
        } finally {
            screenShot(startPage);
        }
    }


}
