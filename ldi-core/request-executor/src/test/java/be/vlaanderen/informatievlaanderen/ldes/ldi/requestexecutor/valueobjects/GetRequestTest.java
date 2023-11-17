package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetRequestTest {

    @Test
    void test_WithUrl() {
        var initialRequest = new GetRequest("url", new RequestHeaders(List.of(new RequestHeader("key", "val"))));

        var requestWithOtherUrl = initialRequest.with("other-url");
        assertEquals(initialRequest.getRequestHeaders(), requestWithOtherUrl.getRequestHeaders());
        assertEquals(initialRequest.getMethod(), requestWithOtherUrl.getMethod());
        assertNotEquals(initialRequest.getUrl(), requestWithOtherUrl.getUrl());
    }

    @Test
    void test_WithRequestHeaders() {
        var initialRequest = new GetRequest("url", new RequestHeaders(List.of(new RequestHeader("key", "val"))));

        var requestWithOtherUrl = initialRequest.with(new RequestHeaders(List.of(new RequestHeader("other-key", "val"))));
        assertNotEquals(initialRequest.getRequestHeaders(), requestWithOtherUrl.getRequestHeaders());
        assertEquals(initialRequest.getMethod(), requestWithOtherUrl.getMethod());
        assertEquals(initialRequest.getUrl(), requestWithOtherUrl.getUrl());
    }

}