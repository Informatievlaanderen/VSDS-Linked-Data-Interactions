package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

public class GetRequest extends Request {

    public static final String METHOD_NAME = "GET";

    public GetRequest(String url, RequestHeaders requestHeaders) {
        super(url, requestHeaders);
    }

    public String getMethod() {
        return METHOD_NAME;
    }

}
