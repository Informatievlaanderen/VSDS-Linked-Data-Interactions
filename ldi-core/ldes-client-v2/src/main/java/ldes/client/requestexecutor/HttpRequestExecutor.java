package ldes.client.requestexecutor;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

/**
 * Handles sending the actual HTTP request to the server.
 */
public class HttpRequestExecutor {
    private final HttpClient httpClient;

    public HttpRequestExecutor(HttpClient httpClient) {
        this.httpClient = httpClient;
    }


    public Response executeRequest(Request request) {
        try {
            HttpResponse<String> httpResponse = httpClient.send(request.createHttpRequest(), HttpResponse.BodyHandlers.ofString());
            return new Response(httpResponse.statusCode(), httpResponse.headers(), httpResponse.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
