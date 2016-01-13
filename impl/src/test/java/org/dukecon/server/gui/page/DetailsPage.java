package org.dukecon.server.gui.page;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DetailsPage extends AbstractPage {

    @Drone
    private WebDriver browser;

    @FindBy(css = ".talk-details")
    private WebElement talkDetails;

    @FindBy(css = ".talk-abstract")
    private WebElement talkAbstract;

    public void verify() {
        Graphene.waitModel().until().element(talkDetails).is().visible();
    }

    public void detailsAreVisible() {
        talkDetails.isDisplayed();
    }

    public void abstractIsVisible() {
        talkAbstract.isDisplayed();
    }
}
