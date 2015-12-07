package org.dukecon.server.gui.stage;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import org.dukecon.server.gui.page.DetailsPage;

public class ThenDetailPage extends AbstractStage<ThenDetailPage> {

    @ProvidedScenarioState
    DetailsPage page;

    public ThenDetailPage talk_details_are_visible() {
        page.detailsAreVisible();
        return this;
    }

    public ThenDetailPage talk_abstract_is_visible() {
        page.abstractIsVisible();
        return this;
    }
}
