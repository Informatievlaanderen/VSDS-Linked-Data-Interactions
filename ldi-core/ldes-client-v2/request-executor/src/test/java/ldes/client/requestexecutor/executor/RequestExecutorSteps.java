package ldes.client.requestexecutor.executor;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.services.RequestExecutorFactory;
import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.exceptions.HttpRequestException;
import ldes.client.requestexecutor.executor.noauth.WireMockConfig;
import org.apache.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestExecutorSteps {

	private final RequestExecutorFactory factory = new RequestExecutorFactory();
	private RequestExecutor requestExecutor;
	private Response response;
	private Request request;
	private RequestHeaders requestHeaders = new RequestHeaders(List.of());

	@Given("I have a ClientCredentialsRequestExecutor")
	public void aClientCredentialsRequestExecutorIsAvailable() {
		requestExecutor = factory.createClientCredentialsExecutor("clientId", "clientSecret",
				"http://localhost:10101/token");
	}

	@Given("I have a DefaultRequestExecutor")
	public void aDefaultRequestExecutorIsAvailable() {
		requestExecutor = factory.createNoAuthExecutor();
	}

	@Then("I obtain a response with status code {int}")
	public void iObtainAResponseWithStatusCode(int arg0) {
		assertEquals(arg0, response.getHttpStatus());
	}

	@And("I obtain a location header: {string}")
	public void iObtainALocationHeader(String url) {
		assertEquals(url, response.getValueOfHeader(HttpHeaders.LOCATION).orElseThrow());
	}

	@Then("I get a HttpRequestException when executing the request")
	public void iGetAnException() {
		assertThrows(HttpRequestException.class, () -> requestExecutor.execute(request));
	}

	@And("I execute the request")
	public void iExecuteTheRequest() {
		response = requestExecutor.execute(request);
	}

	@When("I create RequestHeaders")
	public void iCreateRequestHeaders() {
		requestHeaders = new RequestHeaders(List.of());
	}

	@And("I add a RequestHeader with key {string} and value {string}")
	public void iAddARequestHeaderWithKeyAndValue(String arg0, String arg1) {
		addHeaderToRequestHeaders(arg0, arg1);
	}

	@And("^I create a Request with the RequestHeaders and url: (.*)$")
	public void iCreateARequestWithTheRequestHeadersAndUrl(String arg0) {
		request = new Request(arg0, requestHeaders);
	}

	private void addHeaderToRequestHeaders(String key, String value) {
		List<RequestHeader> headers = new ArrayList<>();
		requestHeaders.forEach(headers::add);
		headers.add(new RequestHeader(key, value));
		requestHeaders = new RequestHeaders(headers);
	}

	@Then("I will have called the token endpoint only once")
	public void iWillHaveCalledTheTokenEndpointOnlyOnce() {
		WireMockConfig.wireMockServer.verify(1, postRequestedFor(urlEqualTo("/token")));
	}

	@Given("I have a requestExecutor which does {int} retries")
	public void iHaveARequestExecutorWhichDoesRetries(int retryCount) {
		requestExecutor = factory.createRetryExecutor(factory.createNoAuthExecutor(), retryCount);
	}

	@Then("I will have called {string} {int} times")
	public void iWillHaveCalledTimes(String arg0, int arg1) {
		WireMockConfig.wireMockServer.verify(arg1, getRequestedFor(urlEqualTo(arg0)));
	}

	@And("I mock {string} to fail the first time and succeed the second time")
	public void iMockToFailTheFirstTimeAndSucceedTheSecondTime(String url) {
		WireMockConfig.wireMockServer.stubFor(get(urlEqualTo(url))
				.inScenario("Retry Scenario")
				.whenScenarioStateIs(STARTED)
				.willReturn(aResponse().withStatus(500))
				.willSetStateTo("Cause Success"));

		WireMockConfig.wireMockServer.stubFor(get(urlEqualTo(url))
				.inScenario("Retry Scenario")
				.whenScenarioStateIs("Cause Success")
				.willReturn(aResponse().withStatus(200)));
	}
}
