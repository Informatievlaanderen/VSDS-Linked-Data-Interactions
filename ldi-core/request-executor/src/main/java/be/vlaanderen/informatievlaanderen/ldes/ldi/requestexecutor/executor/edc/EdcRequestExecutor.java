package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.*;

import java.util.List;

// TODO TVB: 13/11/23 test me
public class EdcRequestExecutor implements RequestExecutor {

    private final RequestExecutor requestExecutor;
    private final TokenService tokenService;

    public EdcRequestExecutor(RequestExecutor requestExecutor, TokenService tokenService) {
        this.requestExecutor = requestExecutor;
        this.tokenService = tokenService;
    }

    // TODO TVB: 14/11/23 test that no accept header is incluced as this fails
    @Override
    public Response execute(Request request) {
        final RequestHeader tokenHeader = tokenService.waitForTokenHeader();
        final var requestHeaders = new RequestHeaders(List.of(tokenHeader));
        final var requestWithToken = new GetRequest(request.getUrl(), requestHeaders);

        final Request newRequest;
        if (GetRequest.METHOD_NAME.equals(request.getMethod())) {
            newRequest = new GetRequest(request.getUrl().replace("http://localhost:8081/devices", "http://localhost:29291/public"), requestHeaders);
        } else {
            newRequest = new PostRequest(request.getUrl(), requestHeaders, ((PostRequest) request).getBody());
        }
        // TODO TVB: 14/11/23 do not hardcode this
        var response = requestExecutor.execute(newRequest);
        if (response.isFobidden()) {
            tokenService.invalidateToken();
            return execute(request);
        } else {
            return response;
        }
    }

}
