package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.RequestHeaders;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.domain.valueobjects.Response;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.wiremock.WireMockConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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

	@Given("I have a ApiKeyRequestExecutor")
	public void aApiKeyRequestExecutorIsAvailable() {
		requestExecutor = factory.createApiKeyExecutor("X-API-KEY", "test123");
	}

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
	public void iObtainAResponseWithStatusCode(int statusCode) {
		assertEquals(statusCode, response.getHttpStatus());
	}

	@And("I obtain a location header: {string}")
	public void iObtainALocationHeader(String locationHeader) {
		assertEquals(locationHeader, response.getFirstHeaderValue(HttpHeaders.LOCATION).orElseThrow());
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
	public void iAddARequestHeaderWithKeyAndValue(String headerKey, String headerValue) {
		addHeaderToRequestHeaders(headerKey, headerValue);
	}

	@And("^I create a Request with the RequestHeaders and url: (.*)$")
	public void iCreateARequestWithTheRequestHeadersAndUrl(String url) {
		request = new Request(url, requestHeaders);
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
		requestExecutor = factory.createRetryExecutor(factory.createNoAuthExecutor(), retryCount, List.of());
	}

	@Given("I have a requestExecutor which does {int} retries with custom http status code {int}")
	public void iHaveARequestExecutorWhichDoesRetries(int retryCount, int httpStatus) {
		requestExecutor = factory.createRetryExecutor(factory.createNoAuthExecutor(), retryCount, List.of(httpStatus));
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
