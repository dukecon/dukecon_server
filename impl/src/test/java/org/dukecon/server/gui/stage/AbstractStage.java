package org.dukecon.server.gui.stage;

import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.IntroWord;
import org.dukecon.server.gui.page.AbstractPage;

/**
 * Base class for all stages. Provides common elements like screenshots and intro words.
 */
public abstract class AbstractStage<SELF extends AbstractStage<?>> {
    @ExpectedScenarioState
    private CurrentStep currentStep;

    protected void screenShot(AbstractPage page) {
        currentStep.addAttachment(page.createScreenshot());
    }

    @IntroWord
    public SELF and() {
        return (SELF) this;
    }
}
