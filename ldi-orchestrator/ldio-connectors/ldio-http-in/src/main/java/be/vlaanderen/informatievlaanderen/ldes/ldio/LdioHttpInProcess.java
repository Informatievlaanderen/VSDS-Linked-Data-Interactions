package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.ApplicationEventPublisher;

public class LdioHttpInProcess extends LdioInput {
	public static final String NAME = "Ldio:HttpIn";
	private boolean isPaused = false;

	public LdioHttpInProcess(String pipelineName, ComponentExecutor executor, LdiAdapter adapter,
							 ObservationRegistry observationRegistry, ApplicationEventPublisher applicationEventPublisher) {
		super(NAME, pipelineName, executor, adapter, observationRegistry, applicationEventPublisher);
	}

	@Override
	protected void resume() {
		this.isPaused = false;
	}

	@Override
	protected void pause() {
		this.isPaused = true;
	}

	public boolean isPaused() {
		return isPaused;
	}

	@Override
	public void shutdown(boolean keepState) {
		// Not implementable for push based
	}
}
