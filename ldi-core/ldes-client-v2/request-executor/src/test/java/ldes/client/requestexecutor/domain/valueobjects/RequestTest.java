package ldes.client.requestexecutor.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    public void testEquals() {
        RequestHeaders headers1 = new RequestHeaders(
                List.of(new RequestHeader("Content-Type", "application/json")));
        RequestHeaders headers1Copy = new RequestHeaders(
                List.of(new RequestHeader("Content-Type", "application/json")));
        RequestHeaders headers2 = new RequestHeaders(new ArrayList<>());
        String url1 = "http://example.com";
        String url2 = "http://example-other.com";

        Request request = new Request(url1, headers1);
        assertEquals(request, request);
        assertEquals(new Request(url1, headers1), new Request(url1, headers1));
        assertEquals(new Request(url1, headers1), new Request(url1, headers1Copy));
        assertNotEquals(new Request(url1, headers1), new Request(url2, headers1));
        assertNotEquals(new Request(url1, headers1), new Request(url1, headers2));
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(new Request(url1, headers1), "String");
    }

    @Test
    public void testHashCode() {
        RequestHeaders headers = new RequestHeaders(new ArrayList<>());
        headers.addHeader(new RequestHeader("Content-Type", "application/json"));

        Request request = new Request("http://example.com", headers);

        assertEquals(request.hashCode(), request.hashCode());
    }

    @Test
    public void testGetUrl() {
        RequestHeaders headers = new RequestHeaders(new ArrayList<>());
        headers.addHeader(new RequestHeader("Content-Type", "application/json"));

        Request request = new Request("http://example.com", headers);

        assertEquals("http://example.com", request.getUrl());
    }

    @Test
    public void testGetRequestHeaders() {
        RequestHeaders headers = new RequestHeaders(new ArrayList<>());
        headers.addHeader(new RequestHeader("Content-Type", "application/json"));

        Request request = new Request("http://example.com", headers);

        assertEquals(headers, request.getRequestHeaders());
    }
}