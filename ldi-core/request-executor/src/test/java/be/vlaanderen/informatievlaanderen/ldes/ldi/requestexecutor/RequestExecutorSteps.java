package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.ratelimiter.RateLimiterConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.retry.RetryConfig;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorDecorator;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.services.RequestExecutorFactory;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.*;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.wiremock.WireMockConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import org.apache.http.HttpHeaders;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.junit.jupiter.api.Assertions.*;

public class RequestExecutorSteps {

	private LocalDateTime start;
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
		request = new GetRequest(url, requestHeaders);
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
		Retry retry = RetryConfig.of(retryCount, List.of()).getRetry();
		requestExecutor = RequestExecutorDecorator.decorate(factory.createNoAuthExecutor()).with(retry).get();
	}

	@Given("I have a requestExecutor which does {int} retries with custom http status code {int}")
	public void iHaveARequestExecutorWhichDoesRetries(int retryCount, int httpStatus) {
		Retry retry = RetryConfig.of(retryCount, List.of(httpStatus)).getRetry();
		requestExecutor = RequestExecutorDecorator.decorate(factory.createNoAuthExecutor()).with(retry).get();
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

	@Given("I have a requestExecutor which limits the requests to 1 per second")
	public void iHaveARequestExecutorWithRateLimiter() {
		Duration waitTime = Duration.ofSeconds(1);
		RateLimiter rateLimiter = new RateLimiterConfig(1, waitTime, waitTime).getRateLimiter();
		requestExecutor = RequestExecutorDecorator.decorate(factory.createNoAuthExecutor()).with(rateLimiter).get();
	}

	@Then("It takes approximately {int} ms to execute the request {int} times")
	public void itTakesSecondsToExecuteTheRequestTimes(int ms, int requestCount) {
		LocalDateTime start = LocalDateTime.now();
		for (int i = 0; i < requestCount; i++) {
			response = requestExecutor.execute(request);
		}
		LocalDateTime end = LocalDateTime.now();

		assertTrue(Duration.between(start, end).toMillis() > ms - 250);
		assertTrue(Duration.between(start, end).toMillis() < ms + 250);
	}

	@Given("I have a requestExecutor which does {int} retries with custom http status code {int} and limits requests")
	public void iHaveARequestExecutorWhichDoesRetriesWithCustomHttpStatusCodeAndLimitsRequests(int retryCount,
			int httpStatus) {
		Duration waitTime = Duration.ofSeconds(1);
		RateLimiter rateLimiter = new RateLimiterConfig(1, waitTime, waitTime).getRateLimiter();
		Retry retry = RetryConfig.of(retryCount, List.of(httpStatus)).getRetry();
		requestExecutor = RequestExecutorDecorator.decorate(factory.createNoAuthExecutor()).with(retry)
				.with(rateLimiter).get();
	}

	@And("I start timing")
	public void iStartTiming() {
		start = LocalDateTime.now();
	}

	@And("Approximately {int} ms have passed")
	public void approximatelyMsHavePassed(int expectedMillisPassed) {
		LocalDateTime end = LocalDateTime.now();
		long actualMillisPassed = Duration.between(start, end).toMillis();
		assertTrue(actualMillisPassed > expectedMillisPassed - 250);
		assertTrue(actualMillisPassed < expectedMillisPassed + 250);
	}
}
