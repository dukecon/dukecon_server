package org.dukecon.server.gui.stage;

import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.IntroWord;
import org.dukecon.server.gui.page.AbstractPage;

/**
 * Base class for all stages. Provides common elements like screenshots and intro words.
 */
public abstract class AbstractStage<SELF extends AbstractStage<?>> {
    @ExpectedScenarioState
    private CurrentStep currentStep;

    @AfterStage
    protected void screenShot() {
        currentStep.addAttachment(AbstractPage.createDirectScreenshot());
    }

    @IntroWord
    public SELF and() {
        screenShot();
        return (SELF) this;
    }
}
