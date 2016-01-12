package org.dukecon.server.gui.stage;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import org.dukecon.server.gui.page.KeycloakLoginPage;

public class WhenLoginPage extends AbstractStage<WhenLoginPage> {

    @ProvidedScenarioState
    private KeycloakLoginPage keycloakLoginPage;

    public WhenLoginPage login_with_user_$1_and_password_$2(@Quoted String user, @Quoted String password) {
        keycloakLoginPage.login(user, password);
        return this;
    }
}
