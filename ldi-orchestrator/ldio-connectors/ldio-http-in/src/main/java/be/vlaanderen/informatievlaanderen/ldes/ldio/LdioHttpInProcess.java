package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationEventPublisher;

public class LdioHttpInProcess extends LdioInput {
	public static final String NAME = "Ldio:HttpIn";

	public LdioHttpInProcess(String pipelineName, ComponentExecutor executor, LdiAdapter adapter,
							 ObservationRegistry observationRegistry, ApplicationEventPublisher applicationEventPublisher) {
		super(NAME, pipelineName, executor, adapter, observationRegistry, applicationEventPublisher);
	}

	@Override
	protected void resume() {
		//Handled by status check
	}

	@Override
	protected void pause() {
		//Handled by status check
	}

	@Override
	public void shutdown() {
		// Not implementable for push based
	}
}
