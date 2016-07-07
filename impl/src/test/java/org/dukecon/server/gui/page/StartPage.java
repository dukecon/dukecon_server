package org.dukecon.server.gui.page;

import com.google.common.base.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.jboss.arquillian.drone.api.annotation.Default;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Location("")
public class StartPage extends AbstractPage {

    @Drone
    private WebDriver browser;

    @FindBy(css = "#days_filter .selected")
    private WebElement selectedDay;

    @FindBy(css = "#days_filter")
    private WebElement daysFilter;

    @FindBy(id = "loading")
    private WebElement loading;

    @FindBy(css = ".talk-info .title")
    private List<WebElement> talks;

    @FindBy(name = "login")
    private WebElement loginLink;

    @FindBy(name = "logout")
    private WebElement logoutLink;

    @FindBy(css = ".username")
    private WebElement username;

    public void verify() {
        browser.manage().window().setSize(new Dimension(1024, 800));
        HasCapabilities c = (HasCapabilities) GrapheneContext.getContextFor(Default.class).getWebDriver();

        Graphene.waitModel().withTimeout(20, TimeUnit.SECONDS).until().element(loading).is().not().visible();

        WebElement acceptCookiesButton = browser.findElement(By.cssSelector(".alert-button button"));
        if (acceptCookiesButton != null && acceptCookiesButton.isDisplayed()) {
            acceptCookiesButton.click();
        }

        Graphene.waitModel().withTimeout(10, TimeUnit.SECONDS).until().element(selectedDay).is().visible();
    }

    public DetailsPage clickOnFirstTalk() {
        assertThat(talks.size()).describedAs("size of talks").isGreaterThanOrEqualTo(1);
        talks.get(0).findElement(By.cssSelector("a")).click();
        return instanceOfPage(DetailsPage.class);
    }

    /**
     * Typed access to the days listed to filter the talks.
     */
    public List<Day> getDays() {
        return daysFilter.findElements(By.cssSelector("button.day-widescreen")).stream()
                .map(e -> new Day(e.getText())).collect(Collectors.toList());
    }

    /**
     * Typed access to all the filters on the left.
     */
    public Map<Filter, List<FilterItem>> getFilters() {
        Map<Filter, List<FilterItem>> result = new HashMap<>();
        List<WebElement> values = browser.findElements(By.cssSelector(".filter-box .filter-values"));
        List<WebElement> labels = browser.findElements(By.cssSelector(".filter-box .filter-category"));
        assertThat(values).hasSameSizeAs(labels);
        for (int i = 0; i < values.size(); ++i) {
            result.put(new Filter(labels.get(i).getText()),
                    values.get(i).findElements(By.cssSelector("label")).stream()
                            .map(e -> new FilterItem(e.getText())).collect(Collectors.toList()));
        }
        return result;
    }

    public KeycloakLoginPage clickOnLogin() {
        loginLink.click();
        Graphene.waitModel().until((Predicate<WebDriver>) webDriver -> {
            // if a logout link becomes visible, logout
            // this might happen if a cookie is left in the browser
            try {
                WebElement logoutLink = browser.findElement(By.name("logout"));
                if (logoutLink != null && logoutLink.isDisplayed()) {
                    logoutLink.click();
                    Graphene.waitModel().until().element(loginLink).is().visible();
                    loginLink.click();
                    return false;
                }
            } catch (NoSuchElementException e) {
                // not found
            }
            // if username field appears, we are fine
            try {
                WebElement usernameField = browser.findElement(By.name("username"));
                if (usernameField != null && usernameField.isDisplayed()) {
                    return true;

                }
            } catch (NoSuchElementException e) {
                // not found
            }
            return false;
        });
        return instanceOfPage(KeycloakLoginPage.class);
    }

    public String getUsername() {
        Graphene.waitModel().withTimeout(15, TimeUnit.SECONDS).until().element(loading).is().not().visible();
        Graphene.waitModel().withTimeout(15, TimeUnit.SECONDS).until().element(username).is().visible();
        return username.getText();
    }

    public boolean isLoggedIn() {
        return logoutLink.isDisplayed();
    }

    public void logoff() {
        logoutLink.click();
        Graphene.waitModel().until().element(loginLink).is().visible();
    }

    @Data
    @AllArgsConstructor
    @ToString
    public static class Day {
        private String day;
    }

    @Data
    @AllArgsConstructor
    @ToString
    public static class Filter {
        private String filter;
    }

    @Data
    @AllArgsConstructor
    @ToString
    public static class FilterItem {
        private String filterItem;
    }
}
