package com.smartbear.readyapi.client.execution;

import com.smartbear.readyapi.client.TestRecipe;
import com.smartbear.readyapi.client.TestRecipeBuilder;
import com.smartbear.readyapi.client.teststeps.TestStepBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static com.smartbear.readyapi.client.support.AssertionUtils.assertExecution;

public class ExecutionUtils {

    private final static Logger LOG = LoggerFactory.getLogger(ExecutionUtils.class);

    private static final String TESTSERVER_ENDPOINT = "testserver.endpoint";
    private static final String TESTSERVER_USER = "testserver.user";
    private static final String TESTSERVER_PASSWORD = "testserver.password";

    private static RecipeExecutor executor;

    public static void executeRecipe(TestStepBuilder... testStepBuilders) {
        TestRecipe recipe = TestRecipeBuilder.newTestRecipe(testStepBuilders).buildTestRecipe();

        if (executor == null) {
            executor = createRecipeExecutor();
        }

        assertExecution(createRecipeExecutor().executeRecipe(recipe));
    }

    private static RecipeExecutor createRecipeExecutor() {
        Map<String, String> env = System.getenv();
        String endpoint = env.getOrDefault(TESTSERVER_ENDPOINT, System.getProperty(TESTSERVER_ENDPOINT));
        if (endpoint != null) {
            try {
                URL url = new URL(endpoint);
                TestServerClient testServerClient = TestServerClient.fromUrl(url.toString());

                String user = env.getOrDefault(TESTSERVER_USER,
                    System.getProperty(TESTSERVER_USER));

                String password = env.getOrDefault(TESTSERVER_PASSWORD,
                    System.getProperty(TESTSERVER_PASSWORD));

                testServerClient.setCredentials(user, password);
                executor = testServerClient.createRecipeExecutor();
                LOG.info("Using TestServer at [" + url.toString() + "] for recipe execution");
            } catch (MalformedURLException e) {
                LOG.error("Failed to create TestServerClient - using local executor instead", e);
            }
        }

        if (executor == null) {
            executor = new SoapUIRecipeExecutor();
            LOG.info("Using Local Recipe Executor");
        }

        return executor;
    }
}
