package org.dukecon.server.gui;

import com.tngtech.jgiven.annotation.ScenarioStage;
import org.dukecon.DukeConServerApplication;
import org.dukecon.server.gui.stage.GivenStartPage;
import org.dukecon.server.gui.stage.ThenStartPage;
import org.dukecon.server.gui.stage.WhenLoginPage;
import org.dukecon.server.gui.stage.WhenStartPage;
import org.dukecon.server.gui.tag.StartPageTag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DukeConServerApplication.class)
@WebIntegrationTest({"server.port=0", "management.port=0"})
public class KeycloakLoginTest extends AbstractPageTest<GivenStartPage, WhenStartPage, ThenStartPage> {

    @ScenarioStage
    private WhenLoginPage whenLoginPage;

    @Test
    @StartPageTag
    public void should_be_able_to_login() throws InterruptedException {
        given()
                .you_use_firefox()
                .and().start_page_opened_in_browser()
                .and().not_logged_in();
        when()
                .click_on_login();
        whenLoginPage
                .login_with_user_$1_and_password_$2("demo@dukecon.org", "demo");
        then()
                .should_show_username("demo@dukecon.org");
    }

}
