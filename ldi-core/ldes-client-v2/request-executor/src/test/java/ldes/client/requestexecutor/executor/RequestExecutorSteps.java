package ldes.client.requestexecutor.executor;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.ClientCredentialsConfig;
import ldes.client.requestexecutor.domain.valueobjects.executorsupplier.DefaultConfig;
import ldes.client.requestexecutor.exceptions.HttpRequestException;
import ldes.client.requestexecutor.executor.noauth.WireMockConfig;
import org.apache.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RequestExecutorSteps {

	private RequestExecutor requestExecutor;
	private Response response;
	private Request request;
	private RequestHeaders requestHeaders;

	@Given("I have a ClientCredentialsRequestExecutor")
	public void aClientCredentialsRequestExecutorIsAvailable() {
		requestExecutor = new ClientCredentialsConfig("clientId", "clientSecret",
				"http://localhost:10101/token").createRequestExecutor();
	}

	@Given("I have a DefaultRequestExecutor")
	public void aDefaultRequestExecutorIsAvailable() {
		requestExecutor = new DefaultConfig().createRequestExecutor();
	}

	@Then("I obtain a response with status code {int}")
	public void iObtainAResponseWithStatusCode(int statusCode) {
		assertEquals(statusCode, response.getHttpStatus());
	}

	@And("I obtain a location header: {string}")
	public void iObtainALocationHeader(String locationHeader) {
		assertEquals(locationHeader, response.getValueOfHeader(HttpHeaders.LOCATION).orElseThrow());
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
}
