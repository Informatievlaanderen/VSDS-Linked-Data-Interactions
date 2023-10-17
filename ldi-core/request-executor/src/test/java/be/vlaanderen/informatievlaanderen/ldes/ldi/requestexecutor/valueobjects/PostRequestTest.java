package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostRequestTest {

    private final static String BODY = "body";
    private final static String URL = "url";

    @Test
    void getMethod() {
        assertEquals("POST", new PostRequest(URL, RequestHeaders.empty(), BODY).getMethod());
    }

    @Test
    void getBody() {
        assertEquals(BODY, new PostRequest(URL, RequestHeaders.empty(), BODY).getBody());
    }

    @Test
    void getContentType() {
        final String contentType = "application/json";
        final List<RequestHeader> requestHeaders = List.of(new RequestHeader("content-type", contentType));
        assertEquals(contentType, new PostRequest(URL, new RequestHeaders(requestHeaders), BODY).getContentType());
    }

}