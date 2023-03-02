package ldes.client.requestexecutor;

import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.Response;

import java.net.http.HttpClient;

public class RequestProcessor {

    public Response processRequest(final Request request) {
        //Insert Retry Logic
        HttpRequestExecutor requestExecutor = new HttpRequestExecutor(HttpClient.newHttpClient());
        return requestExecutor.executeRequest(request);
    }
}
