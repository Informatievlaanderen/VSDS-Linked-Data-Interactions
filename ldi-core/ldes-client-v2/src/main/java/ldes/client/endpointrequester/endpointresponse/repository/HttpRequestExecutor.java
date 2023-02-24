package ldes.client.endpointrequester.endpointresponse.repository;

import ldes.client.endpointrequester.endpoint.Endpoint;
import ldes.client.endpointrequester.endpointresponse.EndpointResponse;
import ldes.client.endpointrequester.endpointresponse.EndpointResponseFactory;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;

public class HttpRequestExecutor implements EndpointResponseRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestExecutor.class);
	private final EndpointResponseFactory endpointResponseFactory;

	public HttpRequestExecutor(EndpointResponseFactory endpointResponseFactory) {
		this.endpointResponseFactory = endpointResponseFactory;
	}

	@Override
	public EndpointResponse getEndpointResponse(Endpoint endpoint) {
		try {
			HttpURLConnection connection = endpoint.httpConnection();
			connection.setRequestMethod(HttpGet.METHOD_NAME);
			connection.setRequestProperty(HttpHeaders.ACCEPT, endpoint.contentType());
			LOGGER.debug("Received response from {}", endpoint.url());
			return endpointResponseFactory.createResponse(connection.getInputStream(), endpoint.lang());
		} catch (Exception e) {
			LOGGER.error("EndpointResponse could not be determined: {} {}", e.getClass().getName(), e.getMessage());
			return endpointResponseFactory.createEmptyResponse();
		}
	}

}
