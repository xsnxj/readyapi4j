package com.smartbear.readyapi4j;

import com.smartbear.readyapi.client.model.GroovyScriptAssertion;
import com.smartbear.readyapi.client.model.InvalidHttpStatusCodesAssertion;
import com.smartbear.readyapi.client.model.JsonPathContentAssertion;
import com.smartbear.readyapi.client.model.JsonPathCountAssertion;
import com.smartbear.readyapi.client.model.ResponseSLAAssertion;
import com.smartbear.readyapi.client.model.RestTestRequestStep;
import com.smartbear.readyapi.client.model.SimpleContainsAssertion;
import com.smartbear.readyapi.client.model.ValidHttpStatusCodesAssertion;
import com.smartbear.readyapi.client.model.XPathContainsAssertion;
import com.smartbear.readyapi.client.model.XQueryContainsAssertion;
import org.junit.Test;

import java.util.Arrays;

import static com.smartbear.readyapi4j.TestRecipeBuilder.newTestRecipe;
import static com.smartbear.readyapi4j.assertions.Assertions.contains;
import static com.smartbear.readyapi4j.assertions.Assertions.invalidStatusCodes;
import static com.smartbear.readyapi4j.assertions.Assertions.maxResponseTime;
import static com.smartbear.readyapi4j.assertions.Assertions.notContains;
import static com.smartbear.readyapi4j.assertions.Assertions.script;
import static com.smartbear.readyapi4j.assertions.Assertions.validStatusCodes;
import static com.smartbear.readyapi4j.assertions.Assertions.xPathContains;
import static com.smartbear.readyapi4j.assertions.Assertions.xQueryContains;
import static com.smartbear.readyapi4j.teststeps.TestSteps.GET;
import static com.smartbear.readyapi4j.teststeps.TestSteps.restRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RestRequestStepWithAssertionRecipeTest {
    private static final String URI = "http://maps.googleapis.com/maps/api/geocode/xml";

    @Test
    public void buildsRestRequestStepRecipeWitJsonPathContentAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe(
                    GET(URI).
                        assertJsonContent("$.results[0].address_components[1].long_name", "Amphitheatre Parkway")
                )
                .buildTestRecipe();

        JsonPathContentAssertion assertion = (JsonPathContentAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("JsonPath Match"));
        assertThat(assertion.getJsonPath(), is("$.results[0].address_components[1].long_name"));
        assertThat(assertion.getExpectedContent(), is("Amphitheatre Parkway"));
        assertThat(assertion.getAllowWildcards(), is(true));
    }

    @Test
    public void buildsRestRequestStepRecipeWitJsonPathCountAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe(
                    GET(URI)
                        .assertJsonCount("$.results[0].address_components[1].long_name", 1)
                )
                .buildTestRecipe();

        JsonPathCountAssertion assertion = (JsonPathCountAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("JsonPath Count"));
        assertThat(assertion.getJsonPath(), is("$.results[0].address_components[1].long_name"));
        assertThat(assertion.getExpectedCount(), is("1"));
        assertThat(assertion.getAllowWildcards(), is(true));
    }

    @Test
    public void buildsRestRequestStepRecipeWithContainsAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe(
                    GET(URI)
                        .addAssertion(contains("bla bla")
                                .ignoreCase()
                                .useRegEx()
                        )
                )
                .buildTestRecipe();

        SimpleContainsAssertion assertion = (SimpleContainsAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("Contains"));
        assertThat(assertion.getToken(), is("bla bla"));
        assertThat(assertion.getIgnoreCase(), is(true));
        assertThat(assertion.getUseRegexp(), is(true));
    }

    @Test
    public void buildsRestRequestStepRecipeWithNotContainsAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe(
                    GET(URI)
                        .addAssertion(notContains("bla bla")
                                .ignoreCase()
                                .useRegEx()
                        )
                )
                .buildTestRecipe();

        SimpleContainsAssertion assertion = (SimpleContainsAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("Not Contains"));
        assertThat(assertion.getToken(), is("bla bla"));
        assertThat(assertion.getIgnoreCase(), is(true));
        assertThat(assertion.getUseRegexp(), is(true));
    }

    @Test
    public void buildsRestRequestStepRecipeWithScriptAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe()
                .addStep(restRequest()
                        .get(URI)
                        .addAssertion(script("return true")
                        )
                )
                .buildTestRecipe();

        GroovyScriptAssertion assertion = (GroovyScriptAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("Script Assertion"));
        assertThat(assertion.getScript(), is("return true"));
    }

    @Test
    public void buildsRestRequestStepRecipeWithValidHttpStatusCodesAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe(
                    GET(URI)
                        .addAssertion(validStatusCodes("202")
                                .withStatusCode(100)
                                .withStatusCodes(Arrays.asList("200", "201"))
                        )
                )
                .buildTestRecipe();

        ValidHttpStatusCodesAssertion assertion = (ValidHttpStatusCodesAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("Valid HTTP Status Codes"));
        assertThat(assertion.getValidStatusCodes(), is(Arrays.asList("202", "100", "200", "201")));
    }

    @Test
    public void buildsRestRequestStepRecipeWithInValidHttpStatusCodesAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe(
                    GET(URI)
                        .addAssertion(invalidStatusCodes()
                                .withStatusCode("100")
                                .withStatusCodes(Arrays.asList("200", "201"))
                        )
                )
                .buildTestRecipe();

        InvalidHttpStatusCodesAssertion assertion = (InvalidHttpStatusCodesAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("Invalid HTTP Status Codes"));
        assertThat(assertion.getInvalidStatusCodes(), is(Arrays.asList("100", "200", "201")));
    }

    @Test
    public void buildsRestRequestStepRecipeWithResponseSLAAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe(
                    GET(URI)
                        .addAssertion(maxResponseTime(1000)
                        )
                )
                .buildTestRecipe();

        ResponseSLAAssertion assertion = (ResponseSLAAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("Response SLA"));
        assertThat(assertion.getMaxResponseTime(), is("1000"));
    }

    @Test
    public void buildsRestRequestStepRecipeWithXPathContainsAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe(
                    GET(URI)
                        .addAssertion(xPathContains("//Addresses/address[0]/name", "Stockholm")
                                .allowWildCards()
                                .ignoreComments()
                                .ignoreNamespaces()
                        )
                )
                .buildTestRecipe();

        XPathContainsAssertion assertion = (XPathContainsAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("XPath Match"));
        assertThat(assertion.getXpath(), is("//Addresses/address[0]/name"));
        assertThat(assertion.getExpectedContent(), is("Stockholm"));
        assertThat(assertion.getAllowWildcards(), is(true));
        assertThat(assertion.getIgnoreComments(), is(true));
        assertThat(assertion.getIgnoreNamespaces(), is(true));
    }

    @Test
    public void buildsRestRequestStepRecipeWithXQueryContainsAssertion() throws Exception {
        TestRecipe recipe = newTestRecipe(
                    GET(URI)
                        .addAssertion(xQueryContains("//Addresses/address[0]/name", "Stockholm")
                                .allowWildcards()
                        )
                )
                .buildTestRecipe();

        XQueryContainsAssertion assertion = (XQueryContainsAssertion) ((RestTestRequestStep) recipe.getTestCase().getTestSteps().get(0)).getAssertions().get(0);
        assertThat(assertion.getType(), is("XQuery Match"));
        assertThat(assertion.getXquery(), is("//Addresses/address[0]/name"));
        assertThat(assertion.getExpectedContent(), is("Stockholm"));
        assertThat(assertion.getAllowWildcards(), is(true));
    }
}
