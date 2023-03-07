package ldes.client.requestexecutor.executor.clientcredentials;

import com.github.scribejava.core.model.OAuthRequest;
import ldes.client.requestexecutor.domain.valueobjects.Request;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeader;
import ldes.client.requestexecutor.domain.valueobjects.RequestHeaders;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientCredentialsRequestTest {

    @Test
    void whenGetOAuthRequest_shouldContainUrlAndHeaders() {
        RequestHeaders requestHeaders =
                new RequestHeaders(
                        List.of(
                                new RequestHeader("key", "value"),
                                new RequestHeader("otherKey", "otherValue")
                        )
                );

        Request request = new Request("url", requestHeaders);
        ClientCredentialsRequest credentialsRequest = new ClientCredentialsRequest(request);
        OAuthRequest oAuthRequest = credentialsRequest.getOAuthRequest();

        assertEquals("url", oAuthRequest.getUrl());
        requestHeaders
                .forEach(header -> assertEquals(header.getValue(), oAuthRequest.getHeaders().get(header.getKey())));
    }
}