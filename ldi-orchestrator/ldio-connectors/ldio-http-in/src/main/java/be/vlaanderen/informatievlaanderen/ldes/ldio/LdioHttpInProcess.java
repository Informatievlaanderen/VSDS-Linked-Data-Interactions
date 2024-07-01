package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.LdioObserver;
import org.springframework.context.ApplicationEventPublisher;

public class LdioHttpInProcess extends LdioInput {
	public static final String NAME = "Ldio:HttpIn";
	private boolean isPaused = false;

	public LdioHttpInProcess(ComponentExecutor executor, LdiAdapter adapter,
							 LdioObserver ldioObserver, ApplicationEventPublisher applicationEventPublisher) {
		super(executor, adapter, ldioObserver, applicationEventPublisher);
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
	public void shutdown() {
		// Not implementable for push based
	}
}
