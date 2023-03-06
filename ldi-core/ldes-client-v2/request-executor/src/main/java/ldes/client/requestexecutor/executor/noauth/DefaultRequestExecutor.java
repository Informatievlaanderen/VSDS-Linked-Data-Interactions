package ldes.client.requestexecutor.executor.noauth;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import ldes.client.requestexecutor.domain.valueobjects.Response;
import ldes.client.requestexecutor.executor.RequestExecutor;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultRequestExecutor implements RequestExecutor {

    private final HttpClient httpClient;

    public DefaultRequestExecutor(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Response apply(Request request) {
        try {
            HttpUriRequest httpRequest = toHttpRequest(request.getRequestHeaders(), request.getUrl());
            return toResponse(httpClient.execute(httpRequest));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpUriRequest toHttpRequest(RequestHeaders headers, String url) {
        final HttpGet request = new HttpGet(url);
        headers.forEach(header -> request.addHeader(header.getKey(), header.getValue()));
        return request;
    }

    private Response toResponse(HttpResponse httpResponse) throws IOException {
        final Map<String, String> headers = extractHeaders(httpResponse.getAllHeaders());
        final int statusCode = httpResponse.getStatusLine().getStatusCode();
        final String body = httpResponse.getEntity() != null ? EntityUtils.toString(httpResponse.getEntity()) : null;
        return new Response(headers, statusCode, body);
    }

    private Map<String, String> extractHeaders(Header[] allHeaders) {
        return Stream.of(allHeaders)
                .collect(
                        Collectors.toMap(Header::getName, Header::getValue, (a, b) -> a + "," + b)
                );
    }

}
