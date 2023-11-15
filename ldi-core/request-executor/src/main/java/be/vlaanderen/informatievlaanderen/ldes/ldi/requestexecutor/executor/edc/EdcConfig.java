package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutorSupplier;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.services.TokenService;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc.valueobjects.EdcUrlProxy;

/**
 * This implementation of the requestExecutor makes it possible to use the LDES Client within
 * the <a href="https://github.com/eclipse-edc">Eclipse Dataspace Components framework</a>.
 */
public class EdcConfig implements RequestExecutorSupplier {

    private final RequestExecutor requestExecutor;
    private final TokenService tokenService;
    private final EdcUrlProxy edcUrlProxy;

    public EdcConfig(RequestExecutor requestExecutor,
                     TokenService tokenService,
                     EdcUrlProxy edcUrlProxy) {
        this.requestExecutor = requestExecutor;
        this.tokenService = tokenService;
        this.edcUrlProxy = edcUrlProxy;
    }

    @Override
    public RequestExecutor createRequestExecutor() {
        return new EdcRequestExecutor(requestExecutor, tokenService, edcUrlProxy);
    }

}
