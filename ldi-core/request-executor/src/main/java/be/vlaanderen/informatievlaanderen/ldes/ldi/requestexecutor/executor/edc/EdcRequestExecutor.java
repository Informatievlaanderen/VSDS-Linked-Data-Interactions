package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.exceptions.HttpRequestException;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Request;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.RequestHeader;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.Response;

// TODO TVB: 13/11/23 test me
public class EdcRequestExecutor implements RequestExecutor {

    private final RequestExecutor requestExecutor;
    private final TokenService tokenService;

    public EdcRequestExecutor(RequestExecutor requestExecutor, TokenService tokenService) {
        this.requestExecutor = requestExecutor;
        this.tokenService = tokenService;
    }

    @Override
    public Response execute(Request request) {
        final RequestHeader tokenHeader = tokenService.waitForTokenHeader();
        final var requestHeaders = request.getRequestHeaders().withRequestHeader(tokenHeader);
        final var requestWithToken = new GetRequest(request.getUrl(), requestHeaders);

        try {
            return requestExecutor.execute(requestWithToken);
        } catch (HttpRequestException ex) {
            // TODO TVB: 13/11/23 check if status is forbidden, else rethrow
            tokenService.invalidateToken();
            return execute(request);
        }
    }

}
