package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcUrlProxy;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.valueobjects.*;

import java.util.List;

public class EdcRequestExecutor implements RequestExecutor {

    private final RequestExecutor requestExecutor;
    private final TokenService tokenService;
    private final EdcUrlProxy urlProxy;

    public EdcRequestExecutor(RequestExecutor requestExecutor, TokenService tokenService, EdcUrlProxy urlProxy) {
        this.requestExecutor = requestExecutor;
        this.tokenService = tokenService;
        this.urlProxy = urlProxy;
    }

    @Override
    public Response execute(Request request) {
        final Request edcRequest = createEdcRequest(request);
        var response = requestExecutor.execute(edcRequest);
        if (response.isFobidden()) {
            tokenService.invalidateToken();
            return execute(request);
        } else {
            return response;
        }
    }

    private Request createEdcRequest(Request request) {
        final var tokenHeader = tokenService.waitForTokenHeader();
        final var requestHeaders = new RequestHeaders(List.of(tokenHeader));
        final var url = urlProxy.proxy(request.getUrl());
        return request.with(url).with(requestHeaders);
    }

}
