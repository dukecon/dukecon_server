package org.jboss.arquillian.junit;

import org.jboss.arquillian.test.spi.LifecycleMethodExecutor;
import org.jboss.arquillian.test.spi.TestRunnerAdaptor;
import org.jboss.arquillian.test.spi.TestRunnerAdaptorBuilder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ArquillianClassRule implements TestRule {

    private TestRunnerAdaptor adaptor;

    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                State.runnerStarted();
                // first time we're being initialized
                if (!State.hasTestAdaptor()) {
                    // no, initialization has been attempted before and failed, refuse
                    // to do anything else
                    if (State.hasInitializationException()) {
                        // failed on suite level, ignore children
                        // notifier.fireTestIgnored(getDescription());
                        throw new RuntimeException(
                                "Arquillian has previously been attempted initialized, but failed. See cause for previous exception",
                                State.getInitializationException());
                    } else {
                        try {
                            // ARQ-1742 If exceptions happen during boot
                            TestRunnerAdaptor adaptor = TestRunnerAdaptorBuilder
                                    .build();
                            // don't set it if beforeSuite fails
                            adaptor.beforeSuite();
                            State.testAdaptor(adaptor);
                        } catch (Exception e) {
                            // caught exception during BeforeSuite, mark this as failed
                            State.caughtInitializationException(e);
                            throw new RuntimeException(e);
                        }
                    }
                }

                // initialization ok, run children
                if (State.hasTestAdaptor()) {
                    adaptor = State.getTestAdaptor();
                }

                adaptor.beforeClass(description.getTestClass(),
                        LifecycleMethodExecutor.NO_OP);
                try {
                    base.evaluate();
                } finally {
                    adaptor.afterClass(description.getTestClass(),
                            LifecycleMethodExecutor.NO_OP);
                    State.runnerFinished();
                    if (State.isLastRunner()) {
                        adaptor.afterSuite();
                        adaptor.shutdown();
                        State.clean();
                    }
                }
            }
        };
    }

}
