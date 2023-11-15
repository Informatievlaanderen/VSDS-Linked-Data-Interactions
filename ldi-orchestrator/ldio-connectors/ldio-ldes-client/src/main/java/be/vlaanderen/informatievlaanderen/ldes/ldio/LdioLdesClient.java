package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class LdioLdesClient extends LdioInput {

	public LdioLdesClient(String componentName, String pipelineName, ComponentExecutor executor, LdesClientRunner ldesClientRunner) {
		super(componentName, pipelineName, executor, null);
		final ExecutorService executorService = newSingleThreadExecutor();
		executorService.submit(ldesClientRunner);
	}
}
