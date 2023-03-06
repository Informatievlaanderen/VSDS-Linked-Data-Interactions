package ldes.client.requestexecutor;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import org.apache.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestProcessorSteps {

	private RequestProcessor requestProcessor;
	private Response response;
	private Request request;
	private String etag;
	private RequestHeaders requestHeaders;

	@Given("I have a RequestProcessor")
	public void initializeCalculator() {
		requestProcessor = new RequestProcessor();
	}

	@Then("I obtain a response with status code {int}")
	public void iObtainAResponseWithStatusCode(int arg0) {
		assertEquals(arg0, response.getHttpStatus());
	}

	@And("I execute the request")
	public void iExecuteTheRequest() {
		response = requestProcessor.processRequest(request);
	}

	@When("I create RequestHeaders")
	public void iCreateRequestHeaders() {
		requestHeaders = new RequestHeaders(List.of());
	}

	@And("I add a RequestHeader with key {string} and value {string}")
	public void iAddARequestHeaderWithKeyAndValue(String arg0, String arg1) {
		addHeaderToRequestHeaders(arg0, arg1);
	}

	@And("I create a Request with the RequestHeaders and url: {string}")
	public void iCreateARequestWithTheRequestHeadersAndUrl(String arg0) {
		request = new Request(arg0, requestHeaders);
	}

	@And("I extract the etag from the response")
	public void iExtractTheEtagFromTheResponse() {
		etag = response.getValueOfHeader(HttpHeaders.ETAG).orElseThrow();
	}

	@And("I add a RequestHeader with key {string} and value the obtained etag")
	public void iAddARequestHeaderWithKeyAndValueTheObtainedEtag(String arg0) {
		addHeaderToRequestHeaders(arg0, etag);
	}

	private void addHeaderToRequestHeaders(String key, String value) {
		List<RequestHeader> headers = new ArrayList<>();
		requestHeaders.forEach(headers::add);
		headers.add(new RequestHeader(key, value));
		requestHeaders = new RequestHeaders(headers);
	}

}
