package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioInput;
import be.vlaanderen.informatievlaanderen.ldes.ldio.types.LdioObserver;

public class LdioHttpInProcess extends LdioInput {
	public static final String NAME = "Ldio:HttpIn";
	private boolean isPaused;

	public LdioHttpInProcess(ComponentExecutor executor, LdiAdapter adapter, LdioObserver ldioObserver) {
		super(executor, adapter, ldioObserver);
	}

	@Override
	public void start() {
		this.isPaused = false;
	}

	@Override
	public void resume() {
		this.isPaused = false;
	}

	@Override
	public void pause() {
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
