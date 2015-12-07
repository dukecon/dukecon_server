package org.dukecon.server.gui.page;

import com.tngtech.jgiven.attachment.Attachment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Base class for all pages. Provides common functionality like screenshots.
 */
public abstract class AbstractPage {

    @Drone
    private WebDriver browser;

    public Attachment createScreenshot() {
        byte[] screenshot = ((TakesScreenshot) browser).getScreenshotAs(OutputType.BYTES);
        return Attachment
                .fromBinaryBytes(screenshot, com.tngtech.jgiven.attachment.MediaType.PNG)
                .withTitle("screenshot " + this.getClass().getSimpleName()
                        + " (" + browser.getCurrentUrl() + ")");
    }

}
