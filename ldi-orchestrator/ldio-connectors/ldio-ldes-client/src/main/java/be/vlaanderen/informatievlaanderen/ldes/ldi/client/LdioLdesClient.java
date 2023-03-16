package be.vlaanderen.informatievlaanderen.ldes.ldi.client;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class LdioLdesClient extends LdiInput {

	public LdioLdesClient(ComponentExecutor executor, LdesClientRunner ldesClientRunner) {
		super(executor, null);
		final ExecutorService executorService = newSingleThreadExecutor();
		try {
			executorService.submit(ldesClientRunner).get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} finally {
			executorService.shutdown();
		}
	}
}
