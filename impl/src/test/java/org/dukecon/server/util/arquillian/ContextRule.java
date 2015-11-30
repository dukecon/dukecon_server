package org.dukecon.server.util.arquillian;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ContextRule implements MethodRule {
    private ContextUrl contextUrl;

    public ContextRule(ContextUrl contextUrl) {
        this.contextUrl = contextUrl;
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                ContextProvider.setUrl(contextUrl.url());
                try {
                    base.evaluate();
                } finally {
                    ContextProvider.setUrl(null);
                }
            }
        };
    }
}
