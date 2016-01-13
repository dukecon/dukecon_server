package org.dukecon.server.gui.stage;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.dukecon.server.gui.page.DetailsPage;
import org.dukecon.server.gui.page.KeycloakLoginPage;
import org.dukecon.server.gui.page.StartPage;

public class WhenStartPage extends AbstractStage<WhenStartPage> {

    @ExpectedScenarioState
    StartPage startPage;

    @ProvidedScenarioState
    DetailsPage detailPage;

    @ProvidedScenarioState
    KeycloakLoginPage keycloakLoginPage;

    public WhenStartPage click_on_first_talk() {
        detailPage = startPage.clickOnFirstTalk();
        return this;
    }

    public WhenStartPage click_on_login() {
        keycloakLoginPage = startPage.clickOnLogin();
        return this;
    }
}
