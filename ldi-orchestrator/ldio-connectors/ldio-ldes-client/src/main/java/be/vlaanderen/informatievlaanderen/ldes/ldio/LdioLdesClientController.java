package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.requestexecutor.executor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.ComponentProperties;
import ldes.client.treenodesupplier.domain.valueobject.StatePersistence;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class LdioLdesClientController {

    private final LdioLdesClient ldesClient;

    private LdioLdesClientController(LdioLdesClient ldesClient) {
        this.ldesClient = ldesClient;
    }

    public static LdioLdesClientController from(String pipelineName,
                                                ComponentExecutor componentExecutor,
                                                RequestExecutor requestExecutor,
                                                ComponentProperties properties,
                                                StatePersistence statePersistence) {
        return new LdioLdesClientController(
                new LdioLdesClient(pipelineName, componentExecutor, requestExecutor, properties, statePersistence)
        );
    }

    public LdioInput getLdioLdesClient() {
        return ldesClient;
    }

    public void start() {
        final ExecutorService executorService = newSingleThreadExecutor();
        executorService.submit(ldesClient::run);
    }

    public void stop() {
        ldesClient.stopThread();
    }

}
