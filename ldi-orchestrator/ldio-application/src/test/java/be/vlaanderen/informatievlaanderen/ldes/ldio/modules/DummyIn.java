package be.vlaanderen.informatievlaanderen.ldes.ldio.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.components.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.services.LdioObserver;
import org.springframework.context.ApplicationEventPublisher;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.status.PipelineStatusTrigger.START;

public class DummyIn extends LdioInput {
	private int counter = 0;

	public DummyIn(ComponentExecutor executor, LdiAdapter adapter, ApplicationEventPublisher applicationEventPublisher) {
		super(executor, adapter, LdioObserver.register("DummyIn", "test", null), applicationEventPublisher);
		this.updateStatus(START);
	}

	public void sendData() {
		String quad = "_:b0 <http://schema.org/integer> \"" + counter++
		              + "\"^^<http://www.w3.org/2001/XMLSchema#integer> .";
		processInput(quad, "application/n-quads");
	}

	@Override
	public void shutdown() {
	}

	@Override
	protected void resume() {

	}

	@Override
	protected void pause() {

	}
}
