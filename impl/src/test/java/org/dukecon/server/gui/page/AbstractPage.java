package org.dukecon.server.gui.page;

import com.tngtech.jgiven.attachment.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.jboss.arquillian.drone.api.annotation.Default;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Base class for all pages. Provides common functionality like screenshots.
 */
@Slf4j
public abstract class AbstractPage {

    @Drone
    private WebDriver browser;

    public static Attachment createDirectScreenshot() {
        WebDriver browser = GrapheneContext.getContextFor(Default.class).getWebDriver();
        byte[] screenshot = ((TakesScreenshot) browser).getScreenshotAs(OutputType.BYTES);
        if (browser instanceof RemoteWebDriver) {
            for (String type : browser.manage().logs().getAvailableLogTypes()) {
                if (type.equals("driver")) {
                    continue;
                }
                for (LogEntry entry : browser.manage().logs().get(type).getAll()) {
                    log.info("type {} log {}", type, entry);
                }
            }
            RemoteWebDriver console = (RemoteWebDriver) browser;
        }
        return Attachment
                .fromBinaryBytes(screenshot, com.tngtech.jgiven.attachment.MediaType.PNG)
                .withTitle("screenshot " + browser.getTitle()
                        + " (" + browser.getCurrentUrl() + ")");
    }

}
