package be.vlaanderen.informatievlaanderen.ldes.ldio.config.modules;

import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiAdapter;
import be.vlaanderen.informatievlaanderen.ldes.ldi.types.LdiInput;

public class DummyIn extends LdiInput {
	private int counter = 0;

	public void sendData() {
		String quad = "_:b0 <http://schema.org/integer> \"" + counter++
				+ "\"^^<http://www.w3.org/2001/XMLSchema#integer> .";
		adapter.apply(LdiAdapter.InputObject.of(quad, "application/n-quads"))
				.forEach(executor::transformLinkedData);
	}
}
