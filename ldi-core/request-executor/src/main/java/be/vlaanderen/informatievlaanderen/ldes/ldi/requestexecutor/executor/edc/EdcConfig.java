package be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.edc;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutorSupplier;

/**
 * This implementation of the requestExecutor makes it possible to use the LDES Client within
 * the <a href="https://github.com/eclipse-edc">Eclipse Dataspace Components framework</a>.
 */
public class EdcConfig implements RequestExecutorSupplier {

    private final RequestExecutor requestExecutor;
    private final TokenService tokenService;

    public EdcConfig(RequestExecutor requestExecutor,
                     TokenService tokenService) {
        this.requestExecutor = requestExecutor;
        this.tokenService = tokenService;
    }

    @Override
    public RequestExecutor createRequestExecutor() {
//        var transferService = new MemoryTransferService(requestExecutor);
//        var tokenService = new MemoryTokenService(transferService);
        return new EdcRequestExecutor(requestExecutor, tokenService);
    }



}
