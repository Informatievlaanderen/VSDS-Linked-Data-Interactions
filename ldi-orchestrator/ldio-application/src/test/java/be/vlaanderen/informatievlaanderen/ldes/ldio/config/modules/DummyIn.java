package be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.services.ComponentExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;

public class DummyIn extends LdiInput {
	private int counter = 0;

	public DummyIn(ComponentExecutor executor, LdiAdapter adapter) {
		super(executor, adapter);
	}

	public void sendData() {
		String quad = "_:b0 <http://schema.org/integer> \"" + counter++
				+ "\"^^<http://www.w3.org/2001/XMLSchema#integer> .";
		getAdapter().apply(LdiAdapter.Content.of(quad, "application/n-quads"))
				.forEach(getExecutor()::transformLinkedData);
	}
}
