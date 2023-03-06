package be.vlaanderen.informatievlaanderen.ldes.ldi.types;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;

public abstract class LdiInput implements LdiComponent {

	private ComponentExecutor executor;
	private LdiAdapter adapter;

	public LdiInput withExecutor(ComponentExecutor executor) {
		this.executor = executor;
		return this;
	}

	public LdiInput withAdapter(LdiAdapter adapter) {
		this.adapter = adapter;
		return this;
	}

	public ComponentExecutor getExecutor() {
		return executor;
	}

	public LdiAdapter getAdapter() {
		return adapter;
	}
}
