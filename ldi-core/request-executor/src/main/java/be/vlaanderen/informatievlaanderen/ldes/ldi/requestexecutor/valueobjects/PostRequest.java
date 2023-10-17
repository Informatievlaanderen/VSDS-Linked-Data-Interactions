package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.apache.http.client.methods.HttpGet;

public class PostRequest extends Request {

    public static final String METHOD_NAME = "POST";

    public PostRequest(String url, RequestHeaders requestHeaders) {
        super(url, requestHeaders);
    }

    public String getMethod() {
        return METHOD_NAME;
    }

}
