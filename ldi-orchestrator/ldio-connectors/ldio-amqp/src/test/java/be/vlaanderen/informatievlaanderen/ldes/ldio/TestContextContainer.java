package be.vlaanderen.informatievlaanderen.ldes.ldio;

import io.cucumber.java.After;
import io.cucumber.java.Before;

public class TestContextContainer {

    private static TestContext testContext = null;

    @After
    @Before
    public static void setupTestContext() {
        testContext = new TestContext();
    }

    public static TestContext getTestContext() {
        return testContext;
    }

}
