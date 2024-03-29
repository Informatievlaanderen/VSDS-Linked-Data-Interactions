package be.vlaanderen.informatievlaanderen.ldes.ldio.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import org.springframework.context.ApplicationEventPublisher;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.valueobjects.PipelineStatus.STARTING;

public class DummyIn extends LdioInput {
	private int counter = 0;

	public DummyIn(ComponentExecutor executor, LdiAdapter adapter, ApplicationEventPublisher applicationEventPublisher) {
		super("DummyIn", "test", executor, adapter, null, applicationEventPublisher);
		this.updateStatus(STARTING);
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
