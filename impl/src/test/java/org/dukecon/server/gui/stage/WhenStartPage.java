package org.dukecon.server.gui.stage;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.dukecon.server.gui.page.DetailsPage;
import org.dukecon.server.gui.page.StartPage;

public class WhenStartPage extends AbstractStage<WhenStartPage> {

    @ExpectedScenarioState
    StartPage startPage;

    @ProvidedScenarioState
    DetailsPage detailPage;

    public void click_on_first_talk() {
        try {
            detailPage = startPage.clickOnFirstTalk();
        } finally {
            screenShot(detailPage != null ? detailPage : startPage);
        }
    }
}
