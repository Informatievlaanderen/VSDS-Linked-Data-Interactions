package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
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
        final var url = request.getUrl().replace("http://localhost:8081/devices", "http://localhost:29291/public");
        return request.with(url).with(requestHeaders);
    }

}
