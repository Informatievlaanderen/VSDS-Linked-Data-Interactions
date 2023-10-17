package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

// TODO TVB: 17/10/23 test auth and no auth flow
public class PostRequest extends Request {

    public static final String METHOD_NAME = "POST";
    private final String body;

    public PostRequest(String url, RequestHeaders requestHeaders, String body) {
        super(url, requestHeaders);
        this.body = body;
    }

    public String getMethod() {
        return METHOD_NAME;
    }

    public String getBody() {
        return body;
    }

    // TODO TVB: 17/10/23 test
    public String getContentType() {
        return getRequestHeaders()
                .getFirst(HttpHeaders.CONTENT_TYPE)
                .map(RequestHeader::getValue)
                .orElse(ContentType.TEXT_PLAIN.getMimeType());
    }

}
