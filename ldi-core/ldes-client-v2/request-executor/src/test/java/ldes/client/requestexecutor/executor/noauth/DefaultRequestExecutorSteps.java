package ldes.client.requestexecutor.executor.noauth;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ldes.client.requestexecutor.domain.valueobjects.DefaultConfig;
import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.exceptions.HttpRequestException;
import ldes.client.requestexecutor.executor.RequestExecutor;
import org.apache.http.HttpHeaders;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultRequestExecutorSteps {

	private RequestExecutor requestExecutor;
	private Response response;
	private Request request;
	private String etag;
	private RequestHeaders requestHeaders;

	@Given("I have a RequestExecutor")
	public void initializeCalculator() {
		requestExecutor = new DefaultConfig().createRequestExecutor();
	}

	@Then("I obtain a response with status code {int}")
	public void iObtainAResponseWithStatusCode(int arg0) {
		assertEquals(arg0, response.getHttpStatus());
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
