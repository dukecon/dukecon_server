package org.dukecon.server.gui;

import org.dukecon.DukeConServerApplication;
import org.dukecon.server.gui.stage.GivenStartPage;
import org.dukecon.server.gui.stage.ThenStartPage;
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
public class StartPageTest extends AbstractPageTest<GivenStartPage, WhenStartPage, ThenStartPage> {

    @Test
    @StartPageTag
    public void should_show_start_page() throws InterruptedException {
        given().start_page_opened_in_browser();
        then().$1_days_should_be_shown(4)
                .and().all_filters_should_have_at_least_$1_values(2);
    }

}
