package org.dukecon.server.gui.stage;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import org.dukecon.server.gui.page.StartPage;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ThenStartPage extends AbstractStage<ThenStartPage> {

    @ExpectedScenarioState
    private StartPage startPage;

    public ThenStartPage $1_days_should_be_shown(int size) {
        assertThat(startPage.getDays()).hasSize(size);
        return this;
    }

    public ThenStartPage $1_levels_should_be_shown(int size) {
        return this;
    }

    public ThenStartPage all_filters_should_have_at_least_$1_values(int minSize) {
        for (Map.Entry<StartPage.Filter, List<StartPage.FilterItem>> e : startPage.getFilters().entrySet()) {
            assertThat(e.getValue().size()).describedAs("size of filter '%s'", e.getKey())
                    .isGreaterThanOrEqualTo(minSize);
        }
        return this;
    }

    public ThenStartPage should_show_username(String username) {
        assertThat(startPage.getUsername()).isEqualTo(username);
        return this;
    }

}
