package com.smartbear.readyapi4j.dsl

import com.smartbear.readyapi.client.model.GroovyScriptAssertion
import com.smartbear.readyapi.client.model.InvalidHttpStatusCodesAssertion
import com.smartbear.readyapi.client.model.JsonPathContentAssertion
import com.smartbear.readyapi.client.model.JsonPathCountAssertion
import com.smartbear.readyapi.client.model.RestTestRequestStep
import com.smartbear.readyapi.client.model.SimpleContainsAssertion
import com.smartbear.readyapi.client.model.ValidHttpStatusCodesAssertion
import com.smartbear.readyapi.client.model.XPathContainsAssertion
import com.smartbear.readyapi4j.TestRecipe
import org.junit.Test

import static TestDsl.recipe
import static com.smartbear.readyapi4j.dsl.DataExtractor.extractFirstTestStep

class RestRequestDslTest {

    public static final String URI = "/uri_to_get"

    @Test
    void buildsRecipeWithGET() throws Exception {
        TestRecipe recipe = recipe {
            GET(URI)
        }

        verifyValuesAndMethod(recipe, 'GET')
    }

    @Test
    void buildsRecipeWithPOST() throws Exception {
        TestRecipe recipe = recipe {
            POST(URI)
        }

        verifyValuesAndMethod(recipe, 'POST')
    }

    @Test
    void buildsRecipeWithPUT() throws Exception {
        TestRecipe recipe = recipe {
            PUT(URI)
        }

        verifyValuesAndMethod(recipe, 'PUT')
    }

    @Test
    void buildsRecipeWithDELETE() throws Exception {
        TestRecipe recipe = recipe {
            DELETE(URI)
        }

        verifyValuesAndMethod(recipe, 'DELETE')
    }

    @Test
    void parameterizesRestRequest() throws Exception {
        String stepName = 'theGET'
        TestRecipe recipe = recipe {
            //Bug in the IntelliJ Groovyc - need parentheses here to make it compile!
            GET ('/some_uri', {
                name stepName
                headers (['Cache-Control': 'nocache'])
                followRedirects true
                entitizeParameters true
                postQueryString true
                timeout 5000
            })
        }

        RestTestRequestStep restRequest = extractFirstTestStep(recipe) as RestTestRequestStep
        assert restRequest.name == stepName
        assert restRequest.headers['Cache-Control'] == ['nocache']
        assert restRequest.followRedirects : 'Not respecting followRedirects'
        assert restRequest.entitizeParameters : 'Not respecting entitizeParameters'
        assert restRequest.postQueryString : 'Not respecting postQueryString'
        assert restRequest.timeout == '5000'
    }

    @Test
    void createsStatusAssertions() throws Exception {
        TestRecipe recipe = recipe {
            //Bug in the IntelliJ Groovyc - need parentheses here to make it compile!
            GET ('/some_uri', {
                assertions {
                    status 200
                    statusNotIn 401,404
                }
            })
        }

        RestTestRequestStep restRequest = extractFirstTestStep(recipe) as RestTestRequestStep
        assert restRequest.assertions.size() == 2
        ValidHttpStatusCodesAssertion statusAssertion = restRequest.assertions[0] as ValidHttpStatusCodesAssertion
        assert statusAssertion.validStatusCodes == ['200']
        InvalidHttpStatusCodesAssertion invalidStatusesAssertion = restRequest.assertions[1] as InvalidHttpStatusCodesAssertion
        assert invalidStatusesAssertion.invalidStatusCodes == ['401', '404']
    }

    @Test
    void createsSimpleContainsAssertions() throws Exception {
        TestRecipe recipe = recipe {
            //Bug in the IntelliJ Groovyc - need parentheses here to make it compile!
            GET ('/some_uri', {
                assertions {
                    responseContains 'Arrival', useRegexp: true, ignoreCase: true
                    responseDoesNotContain 'E.T', useRegexp: true, ignoreCase: true
                    script "assert response.contentType == 'text/xml'"
                }
            })
        }

        RestTestRequestStep restRequest = extractFirstTestStep(recipe) as RestTestRequestStep
        assert restRequest.assertions.size() == 3
        SimpleContainsAssertion containsAssertion = restRequest.assertions[0] as SimpleContainsAssertion
        assert containsAssertion.token == 'Arrival'
        assert containsAssertion.useRegexp : 'Not respecting useRegexp'
        assert containsAssertion.ignoreCase : 'Not respecting ignoreCase'
        assert containsAssertion.type == 'Contains'
        SimpleContainsAssertion notContainsAssertion = restRequest.assertions[1] as SimpleContainsAssertion
        assert notContainsAssertion.token == 'E.T'
        assert notContainsAssertion.useRegexp : 'Not respecting useRegexp'
        assert notContainsAssertion.ignoreCase : 'Not respecting ignoreCase'
        assert notContainsAssertion.type == 'Not Contains'
        GroovyScriptAssertion scriptAssertion = restRequest.assertions[2] as GroovyScriptAssertion
        assert scriptAssertion.script == "assert response.contentType == 'text/xml'"
    }

    @Test
    void createsJsonPathAssertions() throws Exception {
        TestRecipe recipe = recipe {
            //Bug in the IntelliJ Groovyc - need parentheses here to make it compile!
            GET ('/some_uri', {
                assertions {
                    jsonPath '$.customer.address' contains 'Storgatan 1'
                    jsonPath '$.customer.order' occurs 3 times
                }
            })
        }

        RestTestRequestStep restRequest = extractFirstTestStep(recipe) as RestTestRequestStep
        assert restRequest.assertions.size() == 2
        JsonPathContentAssertion containsAssertion = restRequest.assertions[0] as JsonPathContentAssertion
        assert containsAssertion.jsonPath == '$.customer.address'
        assert containsAssertion.expectedContent == 'Storgatan 1'
        JsonPathCountAssertion countAssertion = restRequest.assertions[1] as JsonPathCountAssertion
        assert countAssertion.jsonPath == '$.customer.order'
        assert countAssertion.expectedCount == '3'
    }

    @Test
    void createsXPathAssertions() throws Exception {
        TestRecipe recipe = recipe {
            //Bug in the IntelliJ Groovyc - need parentheses here to make it compile!
            GET ('/some_uri', {
                assertions {
                    xpath '/customer/address' contains 'Storgatan 1'
                    xpath '/customer/order' occurs 3 times
                }
            })
        }

        RestTestRequestStep restRequest = extractFirstTestStep(recipe) as RestTestRequestStep
        assert restRequest.assertions.size() == 2
        XPathContainsAssertion containsAssertion = restRequest.assertions[0] as XPathContainsAssertion
        assert containsAssertion.xpath == '/customer/address'
        assert containsAssertion.expectedContent == 'Storgatan 1'
        XPathContainsAssertion countAssertion = restRequest.assertions[1] as XPathContainsAssertion
        assert countAssertion.xpath == 'count(/customer/order)'
        assert countAssertion.expectedContent == '3'

    }

    private static void verifyValuesAndMethod(TestRecipe recipe, String method) {
        RestTestRequestStep restRequest = extractFirstTestStep(recipe) as RestTestRequestStep
        assert restRequest.URI == URI
        assert restRequest.method == method
    }

}
