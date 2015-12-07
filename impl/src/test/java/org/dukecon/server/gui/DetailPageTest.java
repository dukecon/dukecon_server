package org.dukecon.server.gui;

import org.dukecon.DukeConServerApplication;
import org.dukecon.server.gui.stage.GivenStartPage;
import org.dukecon.server.gui.stage.ThenDetailPage;
import org.dukecon.server.gui.stage.WhenStartPage;
import org.dukecon.server.gui.tag.DetailPageTag;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DukeConServerApplication.class)
@WebIntegrationTest({"server.port=0", "management.port=0"})
public class DetailPageTest extends AbstractPageTest<GivenStartPage, WhenStartPage, ThenDetailPage> {

    @Test
    @DetailPageTag
    public void should_show_details_page() throws InterruptedException {
        given().start_page_opened_in_browser();
        when().click_on_first_talk();
        then().talk_details_are_visible()
                .and().talk_abstract_is_visible();
    }

}
