package org.dukecon.server.gui.page;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.concurrent.TimeUnit;

public class KeycloakLoginPage extends AbstractPage {

    @FindBy(name = "password")
    WebElement passwordField;

    @FindBy(name = "username")
    private WebElement usernameField;

    @FindBy(name = "login")
    private WebElement loginButton;

    @Drone
    private WebDriver browser;

    public void verify() {
        Graphene.waitModel().withTimeout(10, TimeUnit.SECONDS).until().element(usernameField).is().present();
    }

    public void login(String user, String password) {
        usernameField.clear();
        usernameField.sendKeys(user);
        passwordField.clear();
        passwordField.sendKeys(password);
        loginButton.click();
        Graphene.waitModel().withTimeout(10, TimeUnit.SECONDS).until().element(usernameField).is().not().present();
    }
}
